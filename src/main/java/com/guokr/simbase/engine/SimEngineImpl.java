package com.guokr.simbase.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimContext;
import com.guokr.simbase.SimEngine;
import com.guokr.simbase.errors.SimEngineException;
import com.guokr.simbase.errors.SimErrors;
import com.guokr.simbase.errors.SimException;
import com.guokr.simbase.events.BasisListener;
import com.guokr.simbase.events.RecommendationListener;
import com.guokr.simbase.events.VectorSetListener;
import com.guokr.simbase.store.Basis;

public class SimEngineImpl implements SimEngine {

    enum Kind {
        BASIS, VECTORS, RECOMM
    };

    private static final Logger logger = LoggerFactory.getLogger(SimEngineImpl.class);

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
            } catch (Throwable ex) {
                int code = SimErrors.lookup(scope, ex);
                logger.error(SimErrors.info(code), ex);
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
            } catch (SimException ex) {
                String errMsg = ex.getMessage();
                logger.error(errMsg, ex);
                callback.error(errMsg);
            } finally {
                callback.response();
            }
        }
    }

    private SimContext                   context;

    private SimCounter                   counter;

    private Map<String, Kind>            kindOf     = new HashMap<String, Kind>();
    private Map<String, String>          basisOf    = new HashMap<String, String>();
    private Map<String, List<String>>    vectorsOf  = new HashMap<String, List<String>>();
    private Map<String, Set<String>>     rtargetsOf = new HashMap<String, Set<String>>();
    private ExecutorService              mngmExec   = Executors.newSingleThreadExecutor();

    private Map<String, SimBasis>        bases      = new HashMap<String, SimBasis>();
    private Map<String, ExecutorService> dataExecs  = new HashMap<String, ExecutorService>();

    private final Map<String, Integer>   counters   = new HashMap<String, Integer>();
    private final int                    bycount;

    public SimEngineImpl(SimContext simContext) {
        this.context = simContext;
        this.bycount = simContext.getInt("bycount");
        this.loadData();
        this.startCron();
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

    private void validateId(int toCheck) throws SimEngineException {
        if (toCheck < 1) {
            throw new SimEngineException(String.format("Inviad id '%d', should be positive integer", toCheck));
        }
    }

    private void validateProbs(float[] toCheck) throws SimEngineException {
        for (float prob : toCheck) {
            if (prob < 0 || prob > 1) {
                throw new SimEngineException(String.format("Invaid prob '%s', should be between 0 and 1", prob));
            }
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

    private void validateSameBasis(String vkeyTarget, String vkeySource) {
        // TODO
    }

    private String rkey(String vkeySource, String vkeyTarget) {
        return new StringBuilder().append(vkeySource).append("_").append(vkeyTarget).toString();
    }

    private void clearData() {
    }

    private void loadData() {
    }

    private void saveData() {
    }

    private void startCron() {
        final int cronInterval = this.context.getInt("cronInterval");

        Timer cron = new Timer();

        TimerTask cleartask = new TimerTask() {
            public void run() {
                clearData();
            }
        };
        cron.schedule(cleartask, cronInterval / 2, cronInterval);

        TimerTask savetask = new TimerTask() {
            public void run() {
                saveData();
            }
        };
        cron.schedule(savetask, cronInterval, cronInterval);
    }

    @Override
    public void cfg(final SimCallback callback, final String key) {
        mngmExec.execute(new SafeRunner("cfg", callback) {
            @Override
            public void invoke() {
                callback.stringValue(context.getString(key));
            }
        });
    }

    @Override
    public void cfg(final SimCallback callback, final String key, final String val) {
        mngmExec.execute(new SafeRunner("cfg", callback) {
            @Override
            public void invoke() {
                context.put(key, val);
                callback.ok();
            }
        });
    }

    @Override
    public void load(final SimCallback callback, final String bkey) {
        mngmExec.execute(new SafeRunner("load", callback) {
            @Override
            public void invoke() {
                // TODO
                validateKeyFormat(bkey);
                validateNotExistence(bkey);
                callback.ok();
            }
        });
    }

    @Override
    public void save(final SimCallback callback, final String bkey) {
        dataExecs.get(bkey).execute(new SafeRunner("save", callback) {
            @Override
            public void invoke() {
                // TODO
                validateKind("save", bkey, Kind.BASIS);
                callback.ok();
            }
        });
    }

    @Override
    public void xincr(final SimCallback callback, final String vkey, final String key) {
        dataExecs.get(basisOf.get(vkey)).execute(new SafeRunner("xincr", callback) {
            @Override
            public void invoke() {
                validateKind("xincr", vkey, Kind.VECTORS);
                callback.integerValue(counter.incr(vkey, key));
            }
        });
    }

    @Override
    public void xget(final SimCallback callback, final String vkey, final String key) {
        dataExecs.get(basisOf.get(vkey)).execute(new SafeRunner("xget", callback) {
            @Override
            public void invoke() {
                validateKind("xget", vkey, Kind.VECTORS);
                callback.integerValue(counter.get(vkey, key));
            }
        });
    }

    @Override
    public void xlookup(final SimCallback callback, final String vkey, final int vecid) {
        dataExecs.get(basisOf.get(vkey)).execute(new SafeRunner("xlookup", callback) {
            @Override
            public void invoke() {
                validateKind("xlookup", vkey, Kind.VECTORS);
                callback.stringValue(counter.lookup(vkey, vecid));
            }
        });
    }

    @Override
    public void del(final SimCallback callback, final String key) {
        validateExistence(key);
        dataExecs.get(basisOf.get(key)).execute(new AsyncSafeRunner("del") {
            @Override
            public void invoke() {
                if (bases.containsKey(key)) {
                    // TODO
                    // should to be empty before deletion
                } else {
                    // TODO
                }
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
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
    public void bmk(final SimCallback callback, final String bkey, final String[] base) {
        mngmExec.execute(new SafeRunner("bmk", callback) {
            @Override
            public void invoke() {
                validateKeyFormat(bkey);
                Basis basis = new Basis(base);
                bases.put(bkey, new SimBasis(context.getSub("basis", bkey), basis));
                basisOf.put(bkey, bkey);
                kindOf.put(bkey, Kind.BASIS);
                dataExecs.put(bkey, Executors.newSingleThreadExecutor());
                callback.ok();
            }
        });
    }

    @Override
    public void brev(final SimCallback callback, final String bkey, final String[] base) {
        dataExecs.get(bkey).execute(new SafeRunner("brev", callback) {
            @Override
            public void invoke() {
                validateKind("brev", bkey, Kind.BASIS);
                validateKeyFormat(bkey);
                bases.get(bkey).brev(base);
                callback.ok();
            }
        });
    }

    @Override
    public void bget(final SimCallback callback, final String bkey) {
        validateKind("bget", bkey, Kind.BASIS);
        dataExecs.get(bkey).execute(new SafeRunner("bget", callback) {
            @Override
            public void invoke() {
                callback.stringList(bases.get(bkey).bget());
            }
        });
    }

    @Override
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
                callback.stringList((String[]) vkeys.toArray(new String[vkeys.size()]));
            }
        });
    }

    @Override
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
                callback.ok();
            }
        });
    }

    @Override
    public void vids(final SimCallback callback, final String vkey) {
        validateKind("vget", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new SafeRunner("vids", callback) {
            @Override
            public void invoke() {
                callback.integerList(bases.get(bkey).vids(vkey));
            }
        });
    }

    // CURD operations for one vector in vector-set

    @Override
    public void vget(final SimCallback callback, final String vkey, final int vecid) {
        validateKind("vget", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new SafeRunner("vget", callback) {
            @Override
            public void invoke() {
                callback.floatList(bases.get(bkey).vget(vkey, vecid));
            }
        });
    }

    @Override
    public void vadd(final SimCallback callback, final String vkey, final int vecid, final float[] vector) {
        validateKind("vadd", vkey, Kind.VECTORS);
        validateId(vecid);
        validateProbs(vector);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new AsyncSafeRunner("vget") {
            @Override
            public void invoke() {
                bases.get(bkey).vadd(vkey, vecid, vector);

                if (!counters.containsKey(vkey)) {
                    counters.put(vkey, 0);
                }
                int counter = (counters.get(vkey) + 1) % bycount;
                counters.put(vkey, counter);
                if (counter == 0) {
                    logger.info(String.format("adding dense vectors by count of %d", bycount));
                }
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
    public void vset(final SimCallback callback, final String vkey, final int vecid, final float[] vector) {
        validateKind("vset", vkey, Kind.VECTORS);
        validateId(vecid);
        validateProbs(vector);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new SafeRunner("vset", callback) {
            @Override
            public void invoke() {
                bases.get(bkey).vset(vkey, vecid, vector);
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
    public void vacc(final SimCallback callback, final String vkey, final int vecid, final float[] vector) {
        validateKind("vacc", vkey, Kind.VECTORS);
        validateId(vecid);
        validateProbs(vector);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new AsyncSafeRunner("vacc") {
            @Override
            public void invoke() {
                bases.get(bkey).vacc(vkey, vecid, vector);
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
    public void vrem(final SimCallback callback, final String vkey, final int vecid) {
        this.validateKind("vrem", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new AsyncSafeRunner("vrem") {
            @Override
            public void invoke() {
                bases.get(bkey).vrem(vkey, vecid);
            }
        });

        callback.ok();
        callback.response();
    }

    // Internal use for client-side sparsification
    @Override
    public void iget(final SimCallback callback, final String vkey, final int vecid) {
        validateExistence(vkey);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new SafeRunner("iget", callback) {
            @Override
            public void invoke() {
                callback.integerList(bases.get(bkey).iget(vkey, vecid));
            }
        });
    }

    @Override
    public void iadd(SimCallback callback, final String vkey, final int vecid, final int[] pairs) {
        validateKind("iadd", vkey, Kind.VECTORS);
        validateId(vecid);
        final String bkey = basisOf.get(vkey);
        int maxIndex = bases.get(bkey).bget().length;
        validatePairs(maxIndex, pairs);
        dataExecs.get(bkey).execute(new AsyncSafeRunner("iadd") {
            @Override
            public void invoke() {
                bases.get(bkey).iadd(vkey, vecid, pairs);

                if (!counters.containsKey(vkey)) {
                    counters.put(vkey, 0);
                }
                int counter = (counters.get(vkey) + 1) % bycount;
                counters.put(vkey, counter);
                if (counter == 0) {
                    logger.info(String.format("adding sparse vectors by count of %d", bycount));
                }
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
    public void iset(final SimCallback callback, final String vkey, final int vecid, final int[] pairs) {
        validateKind("iset", vkey, Kind.VECTORS);
        validateId(vecid);
        final String bkey = basisOf.get(vkey);
        int maxIndex = bases.get(bkey).bget().length;
        validatePairs(maxIndex, pairs);
        dataExecs.get(bkey).execute(new AsyncSafeRunner("iset") {
            @Override
            public void invoke() {
                bases.get(bkey).iset(vkey, vecid, pairs);
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
    public void iacc(final SimCallback callback, final String vkey, final int vecid, final int[] pairs) {
        this.validateKind("iacc", vkey, Kind.VECTORS);
        validateId(vecid);
        final String bkey = basisOf.get(vkey);
        int maxIndex = bases.get(bkey).bget().length;
        validatePairs(maxIndex, pairs);
        dataExecs.get(bkey).execute(new AsyncSafeRunner("iacc") {
            @Override
            public void invoke() {
                bases.get(bkey).iacc(vkey, vecid, pairs);
            }
        });

        callback.ok();
        callback.response();
    }

    @Override
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
    public void rmk(final SimCallback callback, final String vkeySource, final String vkeyTarget) {
        mngmExec.execute(new SafeRunner("rmk", callback) {
            @Override
            public void invoke() {
                validateKind("rmk", vkeySource, Kind.VECTORS);
                validateKind("rmk", vkeyTarget, Kind.VECTORS);
                validateSameBasis(vkeyTarget, vkeySource);
                String rkey = rkey(vkeyTarget, vkeySource);
                validateNotExistence(rkey);
                final String bkey = basisOf.get(vkeySource);
                bases.get(bkey).rmk(vkeySource, vkeyTarget);
                basisOf.put(rkey, basisOf.get(vkeySource));
                if (rtargetsOf.get(vkeySource) == null) {
                    rtargetsOf.put(vkeySource, new HashSet<String>());
                }
                rtargetsOf.get(vkeySource).add(vkeyTarget);
                callback.ok();
            }
        });
    }

    @Override
    public void rget(final SimCallback callback, final String vkeySource, final int vecid, final String vkeyTarget) {
        validateKind("rget", vkeySource, Kind.VECTORS);
        validateKind("rget", vkeyTarget, Kind.VECTORS);
        String rkey = rkey(vkeyTarget, vkeySource);
        validateExistence(rkey);
        final String bkey = basisOf.get(vkeySource);
        dataExecs.get(bkey).execute(new SafeRunner("rget", callback) {
            @Override
            public void invoke() {
                callback.stringList(bases.get(bkey).rget(vkeySource, vecid, vkeyTarget));
            }
        });
    }

    @Override
    public void rrec(final SimCallback callback, final String vkeySource, final int vecid, final String vkeyTarget) {
        validateKind("rget", vkeySource, Kind.VECTORS);
        validateKind("rget", vkeyTarget, Kind.VECTORS);
        String rkey = rkey(vkeyTarget, vkeySource);
        validateExistence(rkey);
        final String bkey = basisOf.get(vkeySource);
        dataExecs.get(bkey).execute(new SafeRunner("rrec", callback) {
            @Override
            public void invoke() {
                callback.integerList(bases.get(bkey).rrec(vkeySource, vecid, vkeyTarget));
            }
        });
    }

    @Override
    public void listen(final String bkey, final BasisListener listener) {
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                bases.get(bkey).addListener(listener);
            }
        });
    }

    @Override
    public void listen(final String vkey, final VectorSetListener listener) {
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                bases.get(bkey).addListener(vkey, listener);
            }
        });
    }

    @Override
    public void listen(final String srcVkey, final String tgtVkey, final RecommendationListener listener) {
        final String bkey = basisOf.get(srcVkey);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                bases.get(bkey).addListener(srcVkey, tgtVkey, listener);
            }
        });
    }

}
