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
        validateKind("xlookup", vkey, Kind.VECTORS);
        dataExecs.get(basisOf.get(vkey)).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO
                    callback.sendString("");
                } catch (Throwable ex) {
                    int code = SimErrors.lookup("xlookup", ex);
                    logger.error(SimErrors.info(code), ex);
                    callback.sendError(SimErrors.descr(code));
                }
            }
        });
    }

    public void del(final SimEngineCallback callback, String key) {
    }

    public String[] blist(final SimEngineCallback callback) {
        return null;
    }

    public void bmk(final SimEngineCallback callback, String bkey, String[] base) {
    }

    public void brev(final SimEngineCallback callback, String bkey, String[] base) {
    }

    public String[] bget(final SimEngineCallback callback, String bkey) {
        return null;
    }

    public String[] vlist(final SimEngineCallback callback, String bkey) {
        return null;
    }

    public void vmk(final SimEngineCallback callback, String vkey, String bkey) {
    }

    public void vrem(final SimEngineCallback callback, String vkey, int vecid) {
    }

    public void vadd(final SimEngineCallback callback, String vkey, int vecid, float[] distr) {
    }

    public void vacc(final SimEngineCallback callback, String vkey, int vecid, float[] distr) {
    }

    public float[] vget(final SimEngineCallback callback, String vkey, int vecid) {
        return null;
    }

    public void jadd(final SimEngineCallback callback, String vkey, int vecid, String jsonlike) {
    }

    public void jacc(final SimEngineCallback callback, String vkey, int vecid, String jsonlike) {
    }

    public String jget(final SimEngineCallback callback, String vkey, int vecid) {
        return null;
    }

    // Internal use for client-side sparsification
    public void iadd(final SimEngineCallback callback, String vkey, int vecid, int[] pairs) {
    }

    // Internal use for client-side sparsification
    public void iacc(final SimEngineCallback callback, String vkey, int vecid, int[] pairs) {
    }

    // Internal use for client-side sparsification
    public int[] iget(final SimEngineCallback callback, String vkey, int vecid) {
        return null;
    }

    public String[] rlist(final SimEngineCallback callback, String vkey) {
        return null;
    }

    public void rmk(final SimEngineCallback callback, String vkeySource, String vkeyTarget) {
    }

    public String[] rget(final SimEngineCallback callback, String vkeySource, String vkeyTarget) {
        return null;
    }

    public int[] rrec(final SimEngineCallback callback, String vkeySource, String vkeyTarget) {
        return null;
    }

}
