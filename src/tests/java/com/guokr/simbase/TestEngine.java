package com.guokr.simbase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guokr.simbase.engine.SimEngineImpl;

public class TestEngine {

    private static final Logger                   logger = LoggerFactory.getLogger(TestEngine.class);

    private static final Map<Testable, Throwable> errMap = new HashMap<Testable, Throwable>();

    public static SimEngineImpl                   engine;

    public static void execCmd(Object... cmds) throws Throwable {
        for (int i = 0; i < cmds.length; i = i + 2) {
            Testable test = (Testable) cmds[i];
            TestableCallback cb = (TestableCallback) cmds[i + 1];
            exec((Testable) test, cb);
        }
    }

    public static void exec(final Testable t, final TestableCallback cb) throws Throwable {
        final CountDownLatch latch = new CountDownLatch(1);
        final TestableCallback ncb = new TestableCallback() {
            @Override
            public void excepted() {
                cb.excepted();
                this.excepted = cb.excepted;
                try {
                    validate();
                } catch (Throwable e) {
                    errMap.put(t, e);
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }
        };
        t.test(ncb);
        latch.await();
        Throwable e = errMap.get(t);
        errMap.clear();
        if (e != null) {
            throw e;
        }
    }

    public static TestableCallback error(final String msg) {
        return new TestableCallback() {
            @Override
            public void excepted() {
                isError(msg);
            }
        };
    }

    public static TestableCallback ok() {
        return new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
    }

    public static TestableCallback ok(final String msg) {
        return new TestableCallback() {
            @Override
            public void excepted() {
                isOk(msg);
            }
        };
    }

    public static TestableCallback integerValue(final int i) {
        return new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerValue(i);
            }
        };
    }

    public static TestableCallback integerList(final int... args) {
        return new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(args);
            }
        };
    }

    public static TestableCallback longValue(final long l) {
        return new TestableCallback() {
            @Override
            public void excepted() {
                isLongValue(l);
            }
        };
    }

    public static TestableCallback longList(final long... args) {
        return new TestableCallback() {
            @Override
            public void excepted() {
                isLongList(args);
            }
        };
    }

    public static TestableCallback floatValue(final float f) {
        return new TestableCallback() {
            @Override
            public void excepted() {
                isFloatValue(f);
            }
        };
    }

    public static TestableCallback floatList(final float... args) {
        return new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(args);
            }
        };
    }

    public static TestableCallback stringValue(final String s) {
        return new TestableCallback() {
            @Override
            public void excepted() {
                isStringValue(s);
            }
        };
    }

    public static TestableCallback stringList(final String... args) {
        return new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(args);
            }
        };
    }

    public static Testable ping() {
        return null;
    }

    public static Testable info() {
        return null;
    }

    public static Testable load() {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                engine.load(cb);
            }
        };
    }

    public static Testable save() {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                engine.save(cb);
            }
        };
    }

    public static Testable del(final String key) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                engine.del(cb, key);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Testable bload(final String key) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                engine.bload(cb, key);
            }
        };
    }

    public static Testable bsave(final String key) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                engine.bsave(cb, key);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Testable blist() {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s", "blist"));
                engine.blist(cb);
            }
        };
    }

    public static Testable bmk(final String bkey, final String... base) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s", "bmk", bkey, base));
                engine.bmk(cb, bkey, base);
            }
        };
    }

    public static Testable brev(final String bkey, final String... base) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s", "brev", bkey, base));
                engine.brev(cb, bkey, base);
            }
        };
    }

    public static Testable bget(final String bkey) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s", "bget", bkey));
                engine.bget(cb, bkey);
            }
        };
    }

    public static Testable vlist(final String bkey) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s", "vlist", bkey));
                engine.vlist(cb, bkey);
            }
        };
    }

    public static Testable vmk(final String bkey, final String vkey) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s", "vmk", bkey, vkey));
                engine.vmk(cb, bkey, vkey);
            }
        };
    }

    public static Testable vlen(final String vkey) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s", "vlen", vkey));
                engine.vlen(cb, vkey);
            }
        };
    }

    public static Testable vids(final String vkey) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s", "vids", vkey));
                engine.vids(cb, vkey);
            }
        };
    }

    public static Testable vget(final String vkey, final long vecid) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s", "vget", vkey, vecid));
                engine.vget(cb, vkey, vecid);
            }
        };
    }

    public static Testable vadd(final String vkey, final long vecid, final float... vector) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s %s", "vadd", vkey, vecid, vector));
                engine.vadd(cb, vkey, vecid, vector);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Testable vset(final String vkey, final long vecid, final float... vector) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s %s", "vset", vkey, vecid, vector));
                engine.vset(cb, vkey, vecid, vector);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Testable vacc(final String vkey, final long vecid, final float... vector) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s %s", "vacc", vkey, vecid, vector));
                engine.vacc(cb, vkey, vecid, vector);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Testable vrem(final String vkey, final long vecid) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s", "vrem", vkey, vecid));
                engine.vrem(cb, vkey, vecid);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Testable iget(final String vkey, final long vecid) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s", "iget", vkey, vecid));
                engine.iget(cb, vkey, vecid);
            }
        };
    }

    public static Testable iset(final String vkey, final long vecid, final int... pairs) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s %s", "iset", vkey, vecid, pairs));
                engine.iset(cb, vkey, vecid, pairs);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Testable iadd(final String vkey, final long vecid, final int... pairs) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s %s", "iadd", vkey, vecid, pairs));
                engine.iadd(cb, vkey, vecid, pairs);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Testable iacc(final String vkey, final long vecid, final int... pairs) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s %s", "iacc", vkey, vecid, pairs));
                engine.iacc(cb, vkey, vecid, pairs);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Testable rlist(final String vkey) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s", "rlist", vkey));
                engine.rlist(cb, vkey);
            }
        };
    }

    public static Testable rmk(final String vkeySource, final String vkeyTarget, final String funcscore) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s %s", "rmk", vkeySource, vkeyTarget, funcscore));
                engine.rmk(cb, vkeySource, vkeyTarget, funcscore);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Testable rget(final String vkeySource, final long vecid, final String vkeyTarget) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s %s", "rget", vkeySource, vecid, vkeyTarget));
                engine.rget(cb, vkeySource, vecid, vkeyTarget);
            }
        };
    }

    public static Testable rrec(final String vkeySource, final long vecid, final String vkeyTarget) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s %s", "rrec", vkeySource, vecid, vkeyTarget));
                engine.rrec(cb, vkeySource, vecid, vkeyTarget);
            }
        };
    }

    public static Testable xacc(final String vkeyTarget, final long vecidTarget, final String vkeyOperand,
            final long vecidOperand) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String.format("%s %s %s %s %s", "xacc", vkeyTarget, vecidTarget, vkeyOperand, vecidOperand));
                engine.xacc(cb, vkeyTarget, vecidTarget, vkeyOperand, vecidOperand);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Testable xprd(final String vkeyTarget, final long vecidTarget, final String vkeyOperand,
            final long... vecidOperands) {
        return new Testable() {
            @Override
            public void test(TestableCallback cb) {
                logger.info(String
                        .format("%s %s %s %s %s", "xprd", vkeyTarget, vecidTarget, vkeyOperand, vecidOperands));
                engine.xprd(cb, vkeyTarget, vecidTarget, vkeyOperand, vecidOperands);
            }
        };
    }

}
