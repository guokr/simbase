package com.guokr.simbase;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.guokr.simbase.engine.SimEngineImpl;

public class TestEngine {
    public static SimEngineImpl engine;

    public static void execCases(Object... cases) {
    }

    public static void execCase(final TestCallable t, final TestableCallback e) throws Throwable {
        FutureTask<Boolean> future = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                t.expectFor(e);
                return t.call();
            }
        });
        try {
            future.get();
        } catch (ExecutionException ee) {
            throw ee.getCause();
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

    public static TestableCallback integerValue(int i) {
        return null;
    }

    public static TestableCallback integerList(int... args) {
        return null;
    }

    public static TestableCallback floatValue(float f) {
        return null;
    }

    public static TestableCallback floatList(float... args) {
        return null;
    }

    public static TestableCallback stringValue(String f) {
        return null;
    }

    public static TestableCallback stringList(String... args) {
        return null;
    }

    public static TestCallable ping() {
        return null;
    }

    public static TestCallable info() {
        return null;
    }

    public static TestCallable load() {
        return new TestCallable() {
            @Override
            public void test(TestableCallback cb) {
                engine.load(cb);
            }
        };
    }

    public static TestCallable save() {
        return new TestCallable() {
            @Override
            public void test(TestableCallback cb) {
                engine.save(cb);
            }
        };
    }

    public static TestCallable del(final String key) {
        return new TestCallable() {
            @Override
            public void test(TestableCallback cb) {
                engine.del(cb, key);
            }
        };
    }

    public static TestCallable bload(final String key) {
        return new TestCallable() {
            @Override
            public void test(TestableCallback cb) {
                engine.bload(cb, key);
            }
        };
    }

    public static TestCallable bsave(final String key) {
        return new TestCallable() {
            @Override
            public void test(TestableCallback cb) {
                engine.bsave(cb, key);
            }
        };
    }

    public static TestCallable blist() {
        return null;
    }

    public static TestCallable bmk(final String bkey, String... base) {
        return null;
    }

    public static TestCallable brev(final String bkey, String... base) {
        return null;
    }

    public static TestCallable bget(final String bkey) {
        return null;
    }

    public static TestCallable vlist(final String bkey) {
        return null;
    }

    public static TestCallable vmk(final String bkey, String vkey) {
        return null;
    }

    public static TestCallable vlen(final String vkey) {
        return null;
    }

    public static TestCallable vids(final String vkey) {
        return null;
    }

    public static TestCallable vget(final String vkey, int vecid) {
        return null;
    }

    public static TestCallable vadd(final String vkey, int vecid, float... vector) {
        return null;
    }

    public static TestCallable vset(final String vkey, int vecid, float... vector) {
        return null;
    }

    public static TestCallable vacc(final String vkey, int vecid, float... vector) {
        return null;
    }

    public static TestCallable vrem(final String vkey, int vecid) {
        return null;
    }

    public static TestCallable iget(final String vkey, int vecid) {
        return null;
    }

    public static TestCallable iset(final String vkey, int vecid, int... pairs) {
        return null;
    }

    public static TestCallable iadd(final String vkey, int vecid, int... pairs) {
        return null;
    }

    public static TestCallable iacc(final String vkey, int vecid, int... pairs) {
        return null;
    }

    public static TestCallable rlist(final String vkey) {
        return null;
    }

    public static TestCallable rmk(final String vkeySource, String vkeyTarget, String funcscore) {
        return null;
    }

    public static TestCallable rget(final String vkeySource, int vecid, String vkeyTarget) {
        return null;
    }

    public static TestCallable rrec(final String vkeySource, int vecid, String vkeyTarget) {
        return null;
    }

    public static TestCallable xacc(final String vkeyTarget, int vecidTarget, String vkeyOperand, int vecidOperand) {
        return null;
    }

    public static TestCallable xprd(final String vkeyTarget, int vecidTarget, String vkeyOperand, int... vecidOperands) {
        return null;
    }

}
