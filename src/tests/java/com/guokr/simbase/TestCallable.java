package com.guokr.simbase;

import java.util.concurrent.Callable;

public abstract class TestCallable implements Callable<Boolean> {

    private TestableCallback callback;

    public abstract void test(TestableCallback cb);

    public void expectFor(TestableCallback callback) {
        this.callback = callback;
    }

    @Override
    public Boolean call() throws Exception {
        test(this.callback);
        return true;
    }

}
