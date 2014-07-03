package com.guokr.simbase.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guokr.simbase.SimCall;
import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimContext;
import com.guokr.simbase.SimEngine;
import com.guokr.simbase.errors.SimEngineException;
import com.guokr.simbase.events.BasisListener;
import com.guokr.simbase.events.RecommendationListener;
import com.guokr.simbase.events.SimBasisListener;
import com.guokr.simbase.events.VectorSetListener;
import com.guokr.simbase.store.Basis;
import com.guokr.simbase.util.PrefixThreadFactory;

public class SimEngineImpl implements SimEngine, SimBasisListener {

    enum Kind {
        BASIS, VECTORS, RECOMM
    };

    private static final Logger logger         = LoggerFactory.getLogger(SimEngineImpl.class);

    private final AtomicInteger commandCounter = new AtomicInteger();

    public abstract class AsyncSafeRunner implements Runnable {
        String scope;

        public AsyncSafeRunner(String scope) {
            this.scope = scope;
        }

        public abstract void invoke();

        @Override
        public void run() {
            try {
                invoke();
                commandCounter.incrementAndGet();
            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public abstract class SafeRunner implements Runnable {
        SimCallback callback;
        String      scope;

        public SafeRunner(String scope, SimCallback callback) {
            this.scope = scope;
            this.callback = callback;
        }

        public abstract void invoke();

        @Override
        public void run() {
            try {
                invoke();
                commandCounter.incrementAndGet();
            } catch (Throwable ex) {
                String errMsg = ex.getMessage();
                logger.error(errMsg, ex);
                callback.error(errMsg);
            } finally {
                callback.response();
            }
        }
    }

    public class RejectedHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            logger.error("server reject request");
        }

    }

    private SimContext                         context;

    private final ExecutorService              mngmExec    = Executors.newSingleThreadExecutor();
    private final Map<String, Kind>            kindOf      = new HashMap<String, Kind>();
    private final Map<String, String>          basisOf     = new HashMap<String, String>();
    private final Map<String, List<String>>    vectorsOf   = new HashMap<String, List<String>>();
    private final Map<String, Set<String>>     rtargetsOf  = new HashMap<String, Set<String>>();
    private final Map<String, SimBasis>        bases       = new HashMap<String, SimBasis>();

    private final Map<String, ExecutorService> writerExecs = new HashMap<String, ExecutorService>();
    private final ThreadPoolExecutor           readerPool  = new ThreadPoolExecutor(53, 83, 37, TimeUnit.SECONDS,
                                                                   new ArrayBlockingQueue<Runnable>(100),
                                                                   new PrefixThreadFactory("reader"),
                                                                   new RejectedHandler());

    private final Map<String, Integer>         counters    = new HashMap<String, Integer>();
    private final int                          bycount;
    private final String                       savePath;
    private final long                         startTime   = new Date().getTime();

    public SimEngineImpl(SimContext simContext) {
        String separator = System.getProperty("file.separator");
        this.context = simContext;
        this.bycount = simContext.getInt("bycount");
        this.savePath = new StringBuilder(System.getProperty("user.dir")).append(separator)
                .append(context.getString("savepath")).append(separator).toString();
        this.load(null);
        this.startCron();
    }

    private void validatePath(String filePath) throws SimEngineException {
        if (!new File(filePath).exists()) {
            throw new SimEngineException(String.format("Dmp file '%s' not exists", filePath));
        }
    }

    private void validateKeyFormat(String key) throws SimEngineException {
        if (key.indexOf('_') > -1) {
            throw new SimEngineException(String.format("Invalid key format '%s'", key));
        }
    }

    private void validateExistence(String toCheck) throws SimEngineException {
        if (!basisOf.containsKey(toCheck)) {
            throw new SimEngineException(String.format("Unknown data entry '%s'", toCheck));
        }
    }

    private void validateNotExistence(String toCheck) throws SimEngineException {
        if (basisOf.containsKey(toCheck)) {
            throw new SimEngineException(String.format("Data entry '%s' already exists", toCheck));
        }
    }

    private void validateKind(String op, String toCheck, Kind kindShouldBe) throws SimEngineException {
        if (!kindOf.containsKey(toCheck) || !kindShouldBe.equals(kindOf.get(toCheck))) {
            throw new SimEngineException(String.format("Operation '%s' against a non-%s type '%s'", op, kindShouldBe,
                    toCheck));
        }
    }

    private void validateId(long toCheck) throws SimEngineException {
        if (toCheck < 1) {
            throw new SimEngineException(String.format("Inviad id '%d', should be positive integer", toCheck));
        }
    }

    private void validatePairs(int maxIndex, int[] toCheck) throws SimEngineException {
        int len = toCheck.length;
        if (len % 2 != 0) {
            throw new SimEngineException("Sparse vector should be paired");
        }
        for (int offset = 0; offset < len; offset += 2) {
            if (toCheck[offset] < 0 || toCheck[offset] > maxIndex) {
                throw new SimEngineException(String.format("Sparse matrix index '%d' out of bound", toCheck[offset]));
            }
            if (toCheck[offset + 1] < 0) {
                throw new SimEngineException(String.format("Sparse matrix value '%d' should be non-negative",
                        toCheck[offset + 1]));
            }
        }
    }

    private void validateSameBasis(String vkeySource, String vkeyTarget) {
        if (!basisOf.get(vkeySource).equals(basisOf.get(vkeyTarget))) {
            throw new SimEngineException(String.format(
                    "Recommedation[%s_%s] must be between two vector set with same basis", vkeySource, vkeyTarget));
        }
    }

    private String rkey(String vkeySource, String vkeyTarget) {
        return new StringBuilder().append(vkeySource).append("_").append(vkeyTarget).toString();
    }

    public void startCron() {
        final int saveInterval = this.context.getInt("saveinterval");

        Timer cron = new Timer();

        TimerTask savetask = new TimerTask() {
            public void run() {
                save(null);
            }
        };
        cron.schedule(savetask, saveInterval, saveInterval);
    }

    @Override
    @SimCall
    public void info(final SimCallback callback) {
        long curTime = new Date().getTime();
        final long disTime = curTime - startTime;

        Runtime runtime = Runtime.getRuntime();
        long allocatedMemory = runtime.totalMemory();
        final String usedMemory = String.valueOf(allocatedMemory);

        List<String> bkeys = new ArrayList<String>(bases.keySet());
        int tempCount = 0;
        for (String bkey : bkeys) {

            List<String> vkeys = vectorsOf.get(bkey);
            if (vkeys != null) {
                for (String vkey : vkeys) {
                    tempCount += bases.get(bkey).vlen(vkey);
                }
            }
        }
        final int keyCount = tempCount;

        mngmExec.execute(new SafeRunner("info", callback) {
            @Override
            public void invoke() {
                String infos = String.format(
                        "version:%s\nuptime:%s\nused_memory:%s\nvectorKeys:%s\ntotal_command_processed:%s\n", version,
                        disTime / 1000, usedMemory, keyCount, commandCounter.get());
                callback.stringValue(infos);
            }
        });
    }

    @Override
    @SimCall
    public void load(final SimCallback callback) {
        File[] files = new File(savePath).listFiles();
        for (File file : files) {
            String filename = file.getName();
            if (file.isFile() && filename.endsWith("dmp")) {
                bload(null, filename.replaceFirst("[.][^.]+$", ""));
            }
        }

        if (callback != null) {
            callback.ok();
            callback.response();
        }
    }

    @Override
    @SimCall
    public void save(final SimCallback callback) {
        for (String bkey : bases.keySet()) {
            bsave(null, bkey);
        }

        if (callback != null) {
            callback.ok();
            callback.response();
        }
    }

    @Override
    @SimCall
    public void del(final SimCallback callback, final String key) {
        validateExistence(key);
        writerExecs.get(basisOf.get(key)).execute(new AsyncSafeRunner("del") {
            @Override
            public void invoke() {
                delete(key);
            }

            public void delete(String toDel) {
                Kind kind = kindOf.get(toDel);
                switch (kind) {
                case BASIS:
                    if (vectorsOf.containsKey(toDel)) {
                        for (String vec : vectorsOf.get(toDel)) {
                            for (String source : rtargetsOf.keySet()) {
                                if (source.equals(vec)) {
                                    for (String target : rtargetsOf.get(vec)) {
                                        String recKey = rkey(vec, target);
                                        basisOf.remove(recKey);
                                        kindOf.remove(recKey);
                                    }
                                    rtargetsOf.remove(vec);
                                } else {
                                    rtargetsOf.get(source).remove(vec);
                                }
                            }
                            kindOf.remove(vec);
                            basisOf.remove(vec);
                        }
                    }
                    vectorsOf.remove(toDel);
                    kindOf.remove(toDel);
                    basisOf.remove(toDel);
                    bases.remove(toDel);
                    writerExecs.remove(toDel);
                    logger.info(String.format("basis[%s] deleted", toDel));
                    break;
                case VECTORS:
                    String bkey = basisOf.get(toDel);
                    for (String source : rtargetsOf.keySet()) {
                        if (source.equals(toDel)) {
                            for (String target : rtargetsOf.get(toDel)) {
                                delete(rkey(toDel, target));
                            }
                            rtargetsOf.remove(toDel);
                        } else {
                            rtargetsOf.get(source).remove(toDel);
                        }
                    }
                    vectorsOf.get(bkey).remove(toDel);
                    basisOf.remove(toDel);
                    kindOf.remove(toDel);
                    bases.get(bkey).vdel(toDel);
                    logger.info(String.format("vectorset[%s] deleted", toDel));
                    break;
                case RECOMM:
                    bases.get(basisOf.get(toDel)).rdel(toDel);
                    basisOf.remove(toDel);
                    kindOf.remove(toDel);
                    logger.info(String.format("recommendation[%s] deleted", toDel));
                    break;
                }
            }
        });

        if (callback != null) {
            callback.ok();
            callback.response();
        }
    }

    @Override
    @SimCall
    public void bload(final SimCallback callback, final String bkey) {
        mngmExec.execute(new AsyncSafeRunner("bload") {
            @Override
            public void invoke() {
                validateKeyFormat(bkey);
                String filePath = new StringBuilder(savePath).append(bkey).append(".dmp").toString();
                validatePath(filePath);
                if (basisOf.containsKey(bkey)) {
                    del(null, bkey);
                }

                logger.info(String.format("loading basis[%s]", bkey));

                Basis basis = new Basis(bkey);
                SimBasis simBasis = new SimBasis(context.getSub("basis", bkey), basis);
                bases.put(bkey, simBasis);
                basisOf.put(bkey, bkey);
                kindOf.put(bkey, Kind.BASIS);
                simBasis.addListener(SimEngineImpl.this);

                simBasis.bload(filePath);

                writerExecs.put(bkey, Executors.newSingleThreadExecutor());

                logger.info(String.format("basis[%s] loaded", bkey));
            }
        });

        if (callback != null) {
            callback.ok();
            callback.response();
        }
    }

    @Override
    @SimCall
    public void bsave(final SimCallback callback, final String bkey) {
        writerExecs.get(bkey).execute(new AsyncSafeRunner("bsave") {
            @Override
            public void invoke() {
                validateKind("bsave", bkey, Kind.BASIS);

                logger.info(String.format("saving basis[%s]", bkey));

                bases.get(bkey).bsave(new StringBuilder(savePath).append(bkey).append(".dmp").toString());

                logger.info(String.format("basis[%s] saved", bkey));
            }
        });

        if (callback != null) {
            callback.ok();
            callback.response();
        }
    }

    @Override
    @SimCall(callback = "S")
    public void blist(final SimCallback callback) {
        mngmExec.execute(new SafeRunner("blist", callback) {
            @Override
            public void invoke() {
                List<String> bkeys = new ArrayList<String>(bases.keySet());
                Collections.sort(bkeys);
                callback.stringList((String[]) bkeys.toArray(new String[bkeys.size()]));
            }
        });
    }

    @Override
    @SimCall
    public void bmk(final SimCallback callback, final String bkey, final String[] base) {
        mngmExec.execute(new SafeRunner("bmk", callback) {
            @Override
            public void invoke() {
                validateKeyFormat(bkey);
                validateNotExistence(bkey);
                Basis basis = new Basis(bkey, base);
                SimBasis simbasis = new SimBasis(context.getSub("basis", bkey), basis);
                bases.put(bkey, simbasis);
                basisOf.put(bkey, bkey);
                kindOf.put(bkey, Kind.BASIS);
                simbasis.addListener(SimEngineImpl.this);

                writerExecs.put(bkey, Executors.newSingleThreadExecutor());

                logger.info(String.format("basis[%s] created", bkey));

                callback.ok();
            }
        });
    }

    @Override
    @SimCall
    public void brev(final SimCallback callback, final String bkey, final String[] base) {
        writerExecs.get(bkey).execute(new SafeRunner("brev", callback) {
            @Override
            public void invoke() {
                validateKind("brev", bkey, Kind.BASIS);
                validateKeyFormat(bkey);
                bases.get(bkey).brev(base);

                logger.info(String.format("basis[%s] revised", bkey));

                callback.ok();
            }
        });
    }

    @Override
    @SimCall(callback = "S")
    public void bget(final SimCallback callback, final String bkey) {
        validateKind("bget", bkey, Kind.BASIS);
        readerPool.submit(new SafeRunner("bget", callback) {
            @Override
            public void invoke() {
                callback.stringList(bases.get(bkey).bget());
            }
        });
    }

    @Override
    @SimCall(callback = "S")
    public void vlist(final SimCallback callback, final String bkey) {
        mngmExec.execute(new SafeRunner("vlist", callback) {
            @Override
            public void invoke() {
                validateKind("vlist", bkey, Kind.BASIS);
                List<String> vkeys = vectorsOf.get(bkey);
                if (vkeys == null) {
                    vkeys = new ArrayList<String>();
                } else {
                    Collections.sort(vkeys);
                }
                int i = 0;
                String[] result = new String[vkeys.size()];
                for (String key : vkeys) {
                    result[i++] = key;
                }
                callback.stringList(result);
            }
        });
    }

    @Override
    @SimCall
    public void vmk(final SimCallback callback, final String bkey, final String vkey) {
        mngmExec.execute(new SafeRunner("vmk", callback) {
            @Override
            public void invoke() {
                validateKind("vmk", bkey, Kind.BASIS);
                validateKeyFormat(vkey);
                validateNotExistence(vkey);
                bases.get(bkey).vmk(vkey);

                kindOf.put(vkey, Kind.VECTORS);
                basisOf.put(vkey, bkey);
                List<String> vkeys = vectorsOf.get(bkey);
                if (vkeys == null) {
                    vkeys = new ArrayList<String>();
                    vectorsOf.put(bkey, vkeys);
                }
                vkeys.add(vkey);

                logger.info(String.format("vectorset[%s] created under basis[%s]", vkey, bkey));

                callback.ok();
            }
        });
    }

    @Override
    @SimCall(callback = "i")
    public void vlen(final SimCallback callback, final String vkey) {
        validateKind("vget", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        readerPool.submit(new SafeRunner("vids", callback) {
            @Override
            public void invoke() {
                callback.integerValue(bases.get(bkey).vlen(vkey));
            }
        });
    }

    @Override
    @SimCall(callback = "I")
    public void vids(final SimCallback callback, final String vkey) {
        validateKind("vget", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        readerPool.submit(new SafeRunner("vids", callback) {
            @Override
            public void invoke() {
                callback.longList(bases.get(bkey).vids(vkey));
            }
        });
    }

    // CURD operations for one vector in vector-set

    @Override
    @SimCall(callback = "F")
    public void vget(final SimCallback callback, final String vkey, final long vecid) {
        validateKind("vget", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        readerPool.submit(new SafeRunner("vget", callback) {
            @Override
            public void invoke() {
                float[] result = bases.get(bkey).vget(vkey, vecid);
                if (result.length == 0) {
                    callback.nil();
                } else {
                    callback.floatList(result);
                }
            }
        });
    }

    @Override
    @SimCall
    public void vadd(final SimCallback callback, final String vkey, final long vecid, final float[] vector) {
        validateKind("vadd", vkey, Kind.VECTORS);
        validateId(vecid);
        final String bkey = basisOf.get(vkey);
        writerExecs.get(bkey).execute(new AsyncSafeRunner("vadd") {
            @Override
            public void invoke() {
                bases.get(bkey).vadd(vkey, vecid, vector);

                if (!counters.containsKey(vkey)) {
                    counters.put(vkey, 0);
                }
                int counter = counters.get(vkey) + 1;
                counters.put(vkey, counter);
                if (counter % bycount == 0) {
                    logger.info(String.format("adding dense vectors %d to %s", counter, vkey));
                }
                logger.info(String.format("vector[%s] added to vectorset[%s]", vecid, vkey));
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
    @SimCall
    public void vset(final SimCallback callback, final String vkey, final long vecid, final float[] vector) {
        validateKind("vset", vkey, Kind.VECTORS);
        validateId(vecid);
        final String bkey = basisOf.get(vkey);
        writerExecs.get(bkey).execute(new AsyncSafeRunner("vset") {
            @Override
            public void invoke() {
                bases.get(bkey).vset(vkey, vecid, vector);

                if (!counters.containsKey(vkey)) {
                    counters.put(vkey, 0);
                }
                int counter = counters.get(vkey) + 1;
                counters.put(vkey, counter);
                if (counter % bycount == 0) {
                    logger.info(String.format("setting dense vectors %d to %s", counter, vkey));
                }
                logger.info(String.format("vector[%s] setted to vectorset[%s]", vecid, vkey));
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
    @SimCall
    public void vacc(final SimCallback callback, final String vkey, final long vecid, final float[] vector) {
        validateKind("vacc", vkey, Kind.VECTORS);
        validateId(vecid);
        final String bkey = basisOf.get(vkey);
        writerExecs.get(bkey).execute(new AsyncSafeRunner("vacc") {
            @Override
            public void invoke() {
                bases.get(bkey).vacc(vkey, vecid, vector);

                if (!counters.containsKey(vkey)) {
                    counters.put(vkey, 0);
                }
                int counter = counters.get(vkey) + 1;
                counters.put(vkey, counter);
                if (counter % bycount == 0) {
                    logger.info(String.format("acculmulating dense vectors %d to %s", counter, vkey));
                }
                logger.info(String.format("vector[%s] accumulated to vectorset[%s]", vecid, vkey));
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
    @SimCall
    public void vrem(final SimCallback callback, final String vkey, final long vecid) {
        this.validateKind("vrem", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        writerExecs.get(bkey).execute(new AsyncSafeRunner("vrem") {
            @Override
            public void invoke() {
                bases.get(bkey).vrem(vkey, vecid);

                logger.info(String.format("vector[%s] removed from vectorset[%s]", vecid, vkey));
            }
        });

        callback.ok();
        callback.response();
    }

    // Internal use for client-side sparsification
    @Override
    @SimCall(callback = "I")
    public void iget(final SimCallback callback, final String vkey, final long vecid) {
        validateExistence(vkey);
        final String bkey = basisOf.get(vkey);
        readerPool.submit(new SafeRunner("iget", callback) {
            @Override
            public void invoke() {
                callback.integerList(bases.get(bkey).iget(vkey, vecid));
            }
        });
    }

    @Override
    @SimCall
    public void iadd(SimCallback callback, final String vkey, final long vecid, final int[] pairs) {
        validateKind("iadd", vkey, Kind.VECTORS);
        validateId(vecid);
        final String bkey = basisOf.get(vkey);
        int maxIndex = bases.get(bkey).bget().length;
        validatePairs(maxIndex, pairs);
        writerExecs.get(bkey).execute(new AsyncSafeRunner("iadd") {
            @Override
            public void invoke() {
                bases.get(bkey).iadd(vkey, vecid, pairs);

                if (!counters.containsKey(vkey)) {
                    counters.put(vkey, 0);
                }
                int counter = counters.get(vkey) + 1;
                counters.put(vkey, counter);
                if (counter % bycount == 0) {
                    logger.info(String.format("adding sparse vectors %d to %s", counter, vkey));
                }
                logger.info(String.format("sparse vector[%s] added to vectorset[%s]", vecid, vkey));
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
    @SimCall
    public void iset(final SimCallback callback, final String vkey, final long vecid, final int[] pairs) {
        validateKind("iset", vkey, Kind.VECTORS);
        validateId(vecid);
        final String bkey = basisOf.get(vkey);
        int maxIndex = bases.get(bkey).bget().length;
        validatePairs(maxIndex, pairs);
        writerExecs.get(bkey).execute(new AsyncSafeRunner("iset") {
            @Override
            public void invoke() {
                bases.get(bkey).iset(vkey, vecid, pairs);

                if (!counters.containsKey(vkey)) {
                    counters.put(vkey, 0);
                }
                int counter = counters.get(vkey) + 1;
                counters.put(vkey, counter);
                if (counter % bycount == 0) {
                    logger.info(String.format("setting sparse vectors %d to %s", counter, vkey));
                }
                logger.info(String.format("sparse vector[%s] setted to vectorset[%s]", vecid, vkey));
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
    @SimCall
    public void iacc(final SimCallback callback, final String vkey, final long vecid, final int[] pairs) {
        this.validateKind("iacc", vkey, Kind.VECTORS);
        validateId(vecid);
        final String bkey = basisOf.get(vkey);
        int maxIndex = bases.get(bkey).bget().length;
        validatePairs(maxIndex, pairs);
        writerExecs.get(bkey).execute(new AsyncSafeRunner("iacc") {
            @Override
            public void invoke() {
                bases.get(bkey).iacc(vkey, vecid, pairs);

                if (!counters.containsKey(vkey)) {
                    counters.put(vkey, 0);
                }
                int counter = counters.get(vkey) + 1;
                counters.put(vkey, counter);
                if (counter % bycount == 0) {
                    logger.info(String.format("accumulating sparse vectors %d to %s", counter, vkey));
                }
                logger.info(String.format("sparse vector[%s] accumulated to vectorset[%s]", vecid, vkey));
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
    @SimCall(callback = "S")
    public void rlist(final SimCallback callback, final String vkey) {
        mngmExec.execute(new SafeRunner("rlist", callback) {
            @Override
            public void invoke() {
                validateKind("rlist", vkey, Kind.VECTORS);
                List<String> targets = new ArrayList<String>();
                Set<String> tgtSet = rtargetsOf.get(vkey);
                if (tgtSet != null) {
                    targets.addAll(tgtSet);
                    Collections.sort(targets);
                }
                callback.stringList((String[]) targets.toArray(new String[targets.size()]));
            }
        });
    }

    @Override
    @SimCall
    public void rmk(final SimCallback callback, final String vkeySource, final String vkeyTarget, final String funcscore) {
        validateSameBasis(vkeySource, vkeyTarget);
        final String bkey = basisOf.get(vkeySource);
        writerExecs.get(bkey).execute(new SafeRunner("rmk", callback) {
            @Override
            public void invoke() {
                validateKind("rmk", vkeySource, Kind.VECTORS);
                validateKind("rmk", vkeyTarget, Kind.VECTORS);
                validateSameBasis(vkeyTarget, vkeySource);
                String rkey = rkey(vkeySource, vkeyTarget);
                validateNotExistence(rkey);

                logger.info(String.format("creating recommendation[%s_%s] with funcscore[%s]", vkeySource, vkeyTarget,
                        funcscore));

                final String bkey = basisOf.get(vkeySource);
                bases.get(bkey).rmk(vkeySource, vkeyTarget, funcscore);
                basisOf.put(rkey, basisOf.get(vkeySource));
                kindOf.put(rkey, Kind.RECOMM);

                if (rtargetsOf.get(vkeySource) == null) {
                    rtargetsOf.put(vkeySource, new HashSet<String>());
                }
                rtargetsOf.get(vkeySource).add(vkeyTarget);

                logger.info(String.format("recommendation[%s_%s] created with funcscore[%s]", vkeySource, vkeyTarget,
                        funcscore));

                callback.ok();
            }
        });
    }

    @Override
    @SimCall(callback = "S")
    public void rget(final SimCallback callback, final String vkeySource, final long vecid, final String vkeyTarget) {
        validateKind("rget", vkeySource, Kind.VECTORS);
        validateKind("rget", vkeyTarget, Kind.VECTORS);
        String rkey = rkey(vkeySource, vkeyTarget);
        validateExistence(rkey);
        final String bkey = basisOf.get(vkeySource);
        readerPool.submit(new SafeRunner("rget", callback) {
            @Override
            public void invoke() {
                callback.stringList(bases.get(bkey).rget(vkeySource, vecid, vkeyTarget));
            }
        });
    }

    @Override
    @SimCall(callback = "I")
    public void rrec(final SimCallback callback, final String vkeySource, final long vecid, final String vkeyTarget) {
        validateKind("rget", vkeySource, Kind.VECTORS);
        validateKind("rget", vkeyTarget, Kind.VECTORS);
        String rkey = rkey(vkeySource, vkeyTarget);
        validateExistence(rkey);
        final String bkey = basisOf.get(vkeySource);
        readerPool.submit(new SafeRunner("rrec", callback) {
            @Override
            public void invoke() {
                callback.longList(bases.get(bkey).rrec(vkeySource, vecid, vkeyTarget));
            }
        });
    }

    @Override
    @SimCall
    public void xacc(SimCallback callback, final String vkeyTarget, final long vecidTarget, final String vkeyOperand,
            final long vecidOperand) {
        validateKind("xacc", vkeyTarget, Kind.VECTORS);
        validateId(vecidTarget);
        validateKind("xacc", vkeyOperand, Kind.VECTORS);
        validateId(vecidOperand);
        final String bkey = basisOf.get(vkeyTarget);
        writerExecs.get(bkey).execute(new AsyncSafeRunner("xacc") {
            @Override
            public void invoke() {
                SimBasis base = bases.get(bkey);
                float[] vector = base.vget(vkeyOperand, vecidOperand);
                if (vector.length != 0) {
                    base.vacc(vkeyTarget, vecidTarget, vector);

                    if (!counters.containsKey(vkeyTarget)) {
                        counters.put(vkeyTarget, 0);
                    }
                    int counter = counters.get(vkeyTarget) + 1;
                    counters.put(vkeyTarget, counter);
                    if (counter % bycount == 0) {
                        logger.info(String.format("acculmulating dense vectors %d to %s", counter, vkeyTarget));
                    }
                }
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
    @SimCall(callback = "F")
    public void xprd(final SimCallback callback, final String vkeyTarget, final long vecidTarget,
            final String vkeyOperand, final long[] vecidOperands) {
        validateKind("xprd", vkeyTarget, Kind.VECTORS);
        validateId(vecidTarget);
        validateKind("xprd", vkeyOperand, Kind.VECTORS);
        for (long vecidOperand : vecidOperands) {
            validateId(vecidOperand);
        }
        final String bkey = basisOf.get(vkeyTarget);
        writerExecs.get(bkey).execute(new AsyncSafeRunner("xprd") {
            @Override
            public void invoke() {
                SimBasis base = bases.get(bkey);

                int size = vecidOperands.length;
                float[] scores = new float[size];
                float[] target = base.vget(vkeyTarget, vecidTarget);
                for (int i = 0; i < size; i++) {
                    long vecidOperand = vecidOperands[i];
                    float[] operand = base.vget(vkeyOperand, vecidOperand);

                    int len = target.length;
                    float score = 0f, lensq1 = 0f, lensq2 = 0f;
                    for (int j = 0; j < len; j++) {
                        score += target[j] * operand[j];
                    }
                    for (int j = 0; j < len; j++) {
                        lensq1 += target[j] * target[j];
                    }
                    for (int j = 0; j < len; j++) {
                        lensq2 += operand[j] * operand[j];
                    }

                    if (lensq1 > 0 && lensq2 > 0) {
                        scores[i] = (float) (score / Math.sqrt(lensq1) / Math.sqrt(lensq2));
                    } else {
                        scores[i] = 0f;
                    }
                }

                callback.floatList(scores);
                callback.response();
            }
        });
    }

    @Override
    public void listen(final String bkey, final BasisListener listener) {
        writerExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                bases.get(bkey).addListener(listener);
            }
        });
    }

    @Override
    public void listen(final String vkey, final VectorSetListener listener) {
        final String bkey = basisOf.get(vkey);
        writerExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                bases.get(bkey).addListener(vkey, listener);
            }
        });
    }

    @Override
    public void listen(final String srcVkey, final String tgtVkey, final RecommendationListener listener) {
        final String bkey = basisOf.get(srcVkey);
        writerExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                bases.get(bkey).addListener(srcVkey, tgtVkey, listener);
            }
        });
    }

    @Override
    public void onVecSetAdded(String bkeySrc, String vkey) {
        basisOf.put(vkey, bkeySrc);

        List<String> vecs = vectorsOf.get(bkeySrc);
        if (vecs == null) {
            vecs = new ArrayList<String>();
            vectorsOf.put(bkeySrc, vecs);
        }
        vecs.add(vkey);

        kindOf.put(vkey, Kind.VECTORS);
    }

    @Override
    public void onVecSetDeleted(String bkeySrc, String vkey) {
        basisOf.remove(vkey);
        vectorsOf.get(bkeySrc).remove(vkey);
        kindOf.remove(vkey);
    }

    @Override
    public void onRecAdded(String bkeySrc, String vkeyFrom, String vkeyTo) {
        String rkey = rkey(vkeyFrom, vkeyTo);

        basisOf.put(rkey, bkeySrc);

        Set<String> tgt = rtargetsOf.get(vkeyFrom);
        if (tgt == null) {
            tgt = new HashSet<String>();
            rtargetsOf.put(vkeyFrom, tgt);
        }
        tgt.add(vkeyTo);

        kindOf.put(rkey, Kind.RECOMM);
    }

    @Override
    public void onRecDeleted(String bkeySrc, String vkeyFrom, String vkeyTo) {
        String rkey = rkey(vkeyFrom, vkeyTo);
        basisOf.remove(rkey);
        rtargetsOf.get(vkeyFrom).remove(vkeyTo);
        kindOf.remove(rkey);
    }

}
