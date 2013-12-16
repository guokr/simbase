package com.guokr.simbase.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimContext;
import com.guokr.simbase.SimEngine;
import com.guokr.simbase.errors.SimErrors;
import com.guokr.simbase.events.BasisListener;
import com.guokr.simbase.events.RecommendationListener;
import com.guokr.simbase.events.VectorSetListener;
import com.guokr.simbase.store.Basis;

public class SimEngineImpl implements SimEngine {

    enum Kind {
        BASIS, VECTORS, RECOMM
    };

    private static final Logger          logger     = LoggerFactory.getLogger(SimEngineImpl.class);

    private SimContext                   context;

    private SimCounter                   counter;

    private Map<String, Kind>            kindOf     = new HashMap<String, Kind>();
    private Map<String, String>          basisOf    = new HashMap<String, String>();
    private Map<String, List<String>>    vectorsOf  = new HashMap<String, List<String>>();
    private Map<String, List<String>>    rtargetsOf = new HashMap<String, List<String>>();
    private ExecutorService              mngmExec   = Executors.newSingleThreadExecutor();

    private Map<String, SimBasis>        bases      = new HashMap<String, SimBasis>();
    private Map<String, ExecutorService> dataExecs  = new HashMap<String, ExecutorService>();

    public SimEngineImpl(SimContext simContext) {
        this.context = simContext;
        this.loadData();
        this.startCron();
    }

    private void validateKeyFormat(String key) throws IllegalArgumentException {
        if (key.indexOf('_') > -1) {
            throw new IllegalArgumentException("Invalid key format:" + key);
        }
    }

    private void validateExistence(String toCheck) throws IllegalArgumentException {
        if (!basisOf.containsKey(toCheck)) {
            throw new IllegalArgumentException("Data entry[" + toCheck + "] should not exist on server before this operation!");
        }
    }

    private void validateNotExistence(String toCheck) throws IllegalArgumentException {
        if (basisOf.containsKey(toCheck)) {
            throw new IllegalArgumentException("Data entry[" + toCheck + "] should not exist on server before this operation!");
        }
    }

    private void validateKind(String op, String toCheck, Kind kindShouldBe) throws IllegalArgumentException {
        if (!kindOf.containsKey(toCheck) || !kindShouldBe.equals(kindOf.get(toCheck))) {
            throw new IllegalArgumentException("Invalid operation[" + op + "] on kind[" + kindShouldBe + "] with:" + toCheck);
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
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.stringValue(context.getString(key));
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("cfg", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                }
            }
        });
    }

    @Override
    public void cfg(final SimCallback callback, final String key, final String val) {
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    context.put(key, val);
                    callback.ok();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("cfg", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                }
            }
        });
    }

    @Override
    public void load(final SimCallback callback, final String bkey) {
        validateKeyFormat(bkey);
        validateNotExistence(bkey);
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.ok();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("load", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                }
            }
        });
    }

    @Override
    public void save(final SimCallback callback, final String bkey) {
        validateKind("save", bkey, Kind.BASIS);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.ok();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("save", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                }
            }
        });
    }

    @Override
    public void xincr(final SimCallback callback, final String vkey, final String key) {
        validateKind("xincr", vkey, Kind.VECTORS);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.integerValue(counter.incr(vkey, key));
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("xincr", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                }
            }
        });
    }

    @Override
    public void xget(final SimCallback callback, final String vkey, final String key) {
        validateKind("xget", vkey, Kind.VECTORS);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.integerValue(counter.get(vkey, key));
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("xget", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                }
            }
        });
    }

    @Override
    public void xlookup(final SimCallback callback, final String vkey, final int vecid) {
        validateKind("xlookup", vkey, Kind.VECTORS);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.stringValue(counter.lookup(vkey, vecid));
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("xlookup", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                }
            }
        });
    }

    @Override
    public void del(final SimCallback callback, final String key) {
        validateExistence(key);
        dataExecs.get(basisOf.get(key)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bases.containsKey(key)) {
                        // TODO
                        // should to be empty before deletion
                    } else {
                        // TODO
                    }
                    callback.ok();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("del", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                }
            }
        });
    }

    @Override
    public void blist(final SimCallback callback) {
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> bkeys = new ArrayList<String>(bases.keySet());
                    Collections.sort(bkeys);
                    callback.stringList((String[]) bkeys.toArray(new String[bkeys.size()]));
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("blist", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                } finally {
                    callback.flip();
                    callback.response();
                }
            }
        });
    }

    @Override
    public void bmk(final SimCallback callback, final String bkey, final String[] base) {
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    validateKeyFormat(bkey);

                    Basis basis = new Basis(base);

                    bases.put(bkey, new SimBasis(context.getSub("basis", bkey), basis));
                    basisOf.put(bkey, bkey);
                    kindOf.put(bkey, Kind.BASIS);
                    dataExecs.put(bkey, Executors.newSingleThreadExecutor());

                    callback.ok();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("bmk", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                } finally {
                    callback.flip();
                    callback.response();
                }
            }
        });
    }

    @Override
    public void brev(final SimCallback callback, final String bkey, final String[] base) {
        validateKind("brev", bkey, Kind.BASIS);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    validateKeyFormat(bkey);
                    bases.get(bkey).brev(base);
                    callback.ok();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("brev", ex);
                    logger.error(SimErrors.info(code), ex);
                } finally {
                    callback.flip();
                    callback.response();
                }
            }
        });
        callback.ok();
    }

    @Override
    public void bget(final SimCallback callback, final String bkey) {
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    validateKind("bget", bkey, Kind.BASIS);
                    callback.stringList(bases.get(bkey).bget());
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("bget", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                } finally {
                    callback.flip();
                    callback.response();
                }
            }
        });
    }

    @Override
    public void vlist(final SimCallback callback, final String bkey) {
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    validateKind("vlist", bkey, Kind.BASIS);
                    List<String> vkeys = vectorsOf.get(bkey);
                    if (vkeys == null) {
                        vkeys = new ArrayList<String>();
                    } else {
                        Collections.sort(vkeys);
                    }
                    callback.stringList((String[]) vkeys.toArray(new String[vkeys.size()]));
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("vlist", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                } finally {
                    callback.flip();
                    callback.response();
                }
            }
        });
    }

    @Override
    public void vmk(final SimCallback callback, final String bkey, final String vkey) {
        validateKind("vmk", bkey, Kind.BASIS);
        validateKeyFormat(vkey);
        validateNotExistence(vkey);
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bases.get(bkey).vmk(vkey);

                    basisOf.put(vkey, bkey);
                    List<String> vkeys = vectorsOf.get(bkey);
                    if (vkeys == null) {
                        vkeys = new ArrayList<String>();
                        vectorsOf.put(bkey, vkeys);
                    }
                    vkeys.add(vkey);
                    callback.ok();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("vmk", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                } finally {
                    callback.flip();
                    callback.response();
                }
            }
        });
    }

    // CURD operations for one vector in vector-set

    @Override
    public void vget(final SimCallback callback, final String vkey, final int vecid) {
        validateKind("vget", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.floatList(bases.get(bkey).vget(vkey, vecid));
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("vget", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                } finally {
                    callback.flip();
                    callback.response();
                }
            }
        });
    }

    @Override
    public void vadd(final SimCallback callback, final String vkey, final int vecid, final float[] vector) {
        validateKind("vadd", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bases.get(bkey).vadd(vkey, vecid, vector);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("vadd", ex);
                    logger.error(SimErrors.info(code), ex);
                }
            }
        });

        callback.ok();
        callback.flip();
        callback.response();
    }

    @Override
    public void vset(final SimCallback callback, final String vkey, final int vecid, final float[] vector) {
        validateKind("vset", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bases.get(bkey).vset(vkey, vecid, vector);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("vset", ex);
                    logger.error(SimErrors.info(code), ex);
                }
            }
        });

        callback.ok();
        callback.flip();
        callback.response();
    }

    @Override
    public void vacc(final SimCallback callback, final String vkey, final int vecid, final float[] vector) {
        this.validateKind("vacc", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bases.get(bkey).vacc(vkey, vecid, vector);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("vacc", ex);
                    logger.error(SimErrors.info(code), ex);
                }
            }
        });

        callback.ok();
        callback.flip();
        callback.response();
    }

    @Override
    public void vrem(final SimCallback callback, final String vkey, final int vecid) {
        this.validateKind("vrem", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bases.get(bkey).vrem(vkey, vecid);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("vrem", ex);
                    logger.error(SimErrors.info(code), ex);
                }
            }
        });

        callback.ok();
        callback.flip();
        callback.response();
    }

    // Internal use for client-side sparsification
    @Override
    public void iget(final SimCallback callback, final String vkey, final int vecid) {
        validateExistence(vkey);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.integerList(bases.get(bkey).iget(vkey, vecid));
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("iget", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                }
            }
        });
    }

    @Override
    public void iadd(SimCallback callback, final String vkey, final int vecid, final int[] pairs) {
        validateKind("iadd", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bases.get(bkey).iadd(vkey, vecid, pairs);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("iset", ex);
                    logger.error(SimErrors.info(code), ex);
                }
            }
        });

        callback.ok();
        callback.flip();
        callback.response();
    }

    @Override
    public void iset(final SimCallback callback, final String vkey, final int vecid, final int[] pairs) {
        validateKind("iset", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bases.get(bkey).iset(vkey, vecid, pairs);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("iset", ex);
                    logger.error(SimErrors.info(code), ex);
                }
            }
        });

        callback.ok();
        callback.flip();
        callback.response();
    }

    @Override
    public void iacc(final SimCallback callback, final String vkey, final int vecid, final int[] pairs) {
        this.validateKind("iacc", vkey, Kind.VECTORS);
        final String bkey = basisOf.get(vkey);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bases.get(bkey).iacc(vkey, vecid, pairs);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("iacc", ex);
                    logger.error(SimErrors.info(code), ex);
                }
            }
        });
        callback.ok();
    }

    @Override
    public void irem(final SimCallback callback, String vkey, int vecid) {
        vrem(callback, vkey, vecid);
    }

    @Override
    public void rlist(final SimCallback callback, final String vkey) {
        validateKind("rlist", vkey, Kind.VECTORS);
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> targets = rtargetsOf.get(vkey);
                    Collections.sort(targets);
                    callback.stringList((String[]) targets.toArray(new String[targets.size()]));
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("rlist", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                } finally {
                    callback.flip();
                    callback.response();
                }
            }
        });
    }

    @Override
    public void rmk(final SimCallback callback, final String vkeySource, final String vkeyTarget) {
        validateKind("rmk", vkeySource, Kind.VECTORS);
        validateKind("rmk", vkeyTarget, Kind.VECTORS);
        validateSameBasis(vkeyTarget, vkeySource);
        String rkey = rkey(vkeyTarget, vkeySource);
        validateNotExistence(rkey);
        final String bkey = basisOf.get(vkeySource);
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bases.get(bkey).rmk(vkeySource, vkeyTarget);
                    callback.ok();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("rmk", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                } finally {
                    callback.flip();
                    callback.response();
                }
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
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.stringValue(bases.get(bkey).rget(vkeySource, vecid, vkeyTarget));
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("rget", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                } finally {
                    callback.flip();
                    callback.response();
                }
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
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.integerList(bases.get(bkey).rrec(vkeySource, vecid, vkeyTarget));
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("rrec", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.error(SimErrors.descr(code));
                } finally {
                    callback.flip();
                    callback.response();
                }
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
