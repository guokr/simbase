package com.guokr.simbase;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimEngine {

    enum Kind {
        BASIS, VECTORS, RECOMM
    };

    private static final Logger          logger    = LoggerFactory.getLogger(SimEngine.class);

    private SimConfig                    context;

    private Map<String, Kind>            kindOf    = new HashMap<String, Kind>();
    private Map<String, String>          basisOf   = new HashMap<String, String>();
    private ExecutorService              mngmExec  = Executors.newSingleThreadExecutor();

    private Map<String, SimBasis>        bases     = new HashMap<String, SimBasis>();
    private Map<String, ExecutorService> dataExecs = new HashMap<String, ExecutorService>();

    public SimEngine(SimConfig context) {
        this.context = context;
        this.loadData();
        this.startCron();
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

    private boolean checkExistenceAsVectorSet(String vkey) {
        return false;//TODO
    }

    private void clearData() {
    }

    private void loadData() {
    }

    private void saveData() {
    }

    private void startCron() {
        final int cronInterval = this.context.getInt("global", "cronInterval");

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

    public void cfg(final SimEngineCallback callback, final String key, final String property) {
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendCfg(context.getString(key, property));
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("cfg", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void load(final SimEngineCallback callback, final String bkey) {
        validateNotExistence(bkey);
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendOK();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("load", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void save(final SimEngineCallback callback, final String bkey) {
        validateKind("save", bkey, Kind.BASIS);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendOK();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("save", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void xincr(final SimEngineCallback callback, String vkey, String key) {
        validateKind("xincr", vkey, Kind.VECTORS);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendInterger(0);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("xincr", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void xlookup(final SimEngineCallback callback, String vkey, String key) {
        validateKind("xlookup", vkey, Kind.VECTORS);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendInterger(0);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("xlookup", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void xget(final SimEngineCallback callback, String vkey, int vecid) {
        validateKind("xget", vkey, Kind.VECTORS);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendString("");
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("xget", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void del(final SimEngineCallback callback, final String key) {
        validateExistence(key);
        dataExecs.get(basisOf.get(key)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bases.containsKey(key)) {
                        // TODO
                        // force to keep it empty before deletion
                    } else {
                        // TODO
                    }
                    callback.sendOK();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("del", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void blist(final SimEngineCallback callback) {
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendOK();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("blist", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void bmk(final SimEngineCallback callback, String bkey, String[] base) {
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendOK();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("bmk", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void brev(final SimEngineCallback callback, String bkey, String[] base) {
        validateKind("brev", bkey, Kind.BASIS);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("brev", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
        callback.sendOK();
    }

    public void bget(final SimEngineCallback callback, String bkey) {
        validateKind("bget", bkey, Kind.BASIS);
        dataExecs.get(bkey).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendStringList(null);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("bget", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void vlist(final SimEngineCallback callback, String bkey) {
        validateKind("vlist", bkey, Kind.BASIS);
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendStringList(null);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("vlist", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void vmk(final SimEngineCallback callback, String bkey, String vkey) {
        validateKind("vmk", bkey, Kind.BASIS);
        validateNotExistence(vkey);
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendOK();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("vmk", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    // CURD operations for one vector in vector-set

    public void vget(final SimEngineCallback callback, String vkey, int vecid) {
        validateExistence(vkey);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendFloatList(null);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("vget", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void vset(final SimEngineCallback callback, final String vkey, int vecid, float[] distr) {
        boolean exists = checkExistenceAsVectorSet(vkey);
        if (!exists) {
            mngmExec.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // TODO
                        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // TODO
                                } catch (Throwable ex) {
                                    int code = SimErrors.lookup("vset", ex);
                                    logger.error(SimErrors.info(code), ex);
                                    callback.sendError(SimErrors.descr(code));
                                }
                            }
                        });
                        callback.sendOK();
                    } catch (Throwable ex) {
                        int code = SimErrors.lookup("vset", ex);
                        logger.error(SimErrors.info(code), ex);
                        callback.sendError(SimErrors.descr(code));
                    }
                }
            });
        } else {
            dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // TODO
                    } catch (Throwable ex) {
                        int code = SimErrors.lookup("vset", ex);
                        logger.error(SimErrors.info(code), ex);
                        callback.sendError(SimErrors.descr(code));
                    }
                }
            });
            callback.sendOK();
        }
    }

    public void vacc(final SimEngineCallback callback, String vkey, int vecid, float[] distr) {
        this.validateKind("vacc", vkey, Kind.VECTORS);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendFloatList(null);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("vacc", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void vrem(final SimEngineCallback callback, String vkey, int vecid) {
        this.validateKind("vacc", vkey, Kind.VECTORS);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendFloatList(null);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("vrem", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void jget(final SimEngineCallback callback, String vkey, int vecid) {
        validateExistence(vkey);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendFloatList(null);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("jget", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void jset(final SimEngineCallback callback, final String vkey, int vecid, String jsonlike) {
        boolean exists = checkExistenceAsVectorSet(vkey);
        if (!exists) {
            mngmExec.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // TODO
                        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // TODO
                                } catch (Throwable ex) {
                                    int code = SimErrors.lookup("jset", ex);
                                    logger.error(SimErrors.info(code), ex);
                                    callback.sendError(SimErrors.descr(code));
                                }
                            }
                        });
                        callback.sendOK();
                    } catch (Throwable ex) {
                        int code = SimErrors.lookup("jset", ex);
                        logger.error(SimErrors.info(code), ex);
                        callback.sendError(SimErrors.descr(code));
                    }
                }
            });
        } else {
            dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // TODO
                    } catch (Throwable ex) {
                        int code = SimErrors.lookup("jset", ex);
                        logger.error(SimErrors.info(code), ex);
                        callback.sendError(SimErrors.descr(code));
                    }
                }
            });
            callback.sendOK();
        }
    }

    public void jacc(final SimEngineCallback callback, String vkey, int vecid, String jsonlike) {
        this.validateKind("jacc", vkey, Kind.VECTORS);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendFloatList(null);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("jacc", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void jrem(final SimEngineCallback callback, String vkey, int vecid) {
        this.validateKind("jrem", vkey, Kind.VECTORS);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendFloatList(null);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("jrem", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    // Internal use for client-side sparsification
    public void iget(final SimEngineCallback callback, String vkey, int vecid) {
        validateExistence(vkey);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendFloatList(null);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("iget", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    // Internal use for client-side sparsification
    public void iset(final SimEngineCallback callback, final String vkey, int vecid, int[] pairs) {
        boolean exists = checkExistenceAsVectorSet(vkey);
        if (!exists) {
            mngmExec.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // TODO
                        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // TODO
                                } catch (Throwable ex) {
                                    int code = SimErrors.lookup("iset", ex);
                                    logger.error(SimErrors.info(code), ex);
                                    callback.sendError(SimErrors.descr(code));
                                }
                            }
                        });
                        callback.sendOK();
                    } catch (Throwable ex) {
                        int code = SimErrors.lookup("iset", ex);
                        logger.error(SimErrors.info(code), ex);
                        callback.sendError(SimErrors.descr(code));
                    }
                }
            });
        } else {
            dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // TODO
                    } catch (Throwable ex) {
                        int code = SimErrors.lookup("iset", ex);
                        logger.error(SimErrors.info(code), ex);
                        callback.sendError(SimErrors.descr(code));
                    }
                }
            });
            callback.sendOK();
        }
    }

    // Internal use for client-side sparsification
    public void iacc(final SimEngineCallback callback, String vkey, int vecid, int[] pairs) {
        this.validateKind("iacc", vkey, Kind.VECTORS);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("iacc", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
        callback.sendOK();
    }

    // Internal use for client-side sparsification
    public void irem(final SimEngineCallback callback, String vkey, int vecid) {
        this.validateKind("irem", vkey, Kind.VECTORS);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("irem", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
        callback.sendOK();
    }

    public void rlist(final SimEngineCallback callback, String vkey) {
        validateKind("rlist", vkey, Kind.VECTORS);
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendStringList(null);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("rlist", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void rmk(final SimEngineCallback callback, String vkeySource, String vkeyTarget) {
        validateKind("rmk", vkeySource, Kind.VECTORS);
        validateKind("rmk", vkeyTarget, Kind.VECTORS);
        validateSameBasis(vkeyTarget, vkeySource);
        validateNotExistence(vkeyTarget + ":" + vkeySource);
        mngmExec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendOK();
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("rmk", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void rget(final SimEngineCallback callback, String vkeySource, String vkeyTarget) {
        validateKind("rget", vkeySource, Kind.VECTORS);
        validateKind("rget", vkeyTarget, Kind.VECTORS);
        validateExistence(vkeyTarget + ":" + vkeySource);
        dataExecs.get(basisOf.get(vkeySource)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendString(null);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("rget", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void rrec(final SimEngineCallback callback, String vkeySource, String vkeyTarget) {
        validateKind("rget", vkeySource, Kind.VECTORS);
        validateKind("rget", vkeyTarget, Kind.VECTORS);
        validateExistence(vkeyTarget + ":" + vkeySource);
        dataExecs.get(basisOf.get(vkeySource)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendIntegerList(null);
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("rrec", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

}
