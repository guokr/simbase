package com.guokr.simbase;


public abstract class SimCommand {

    public void invoke(SimEngine engine, SimCallback callback) {
        throw new IllegalArgumentException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, Object arg1, SimCallback callback) {
        throw new IllegalArgumentException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, Object arg1, Object arg2, SimCallback callback) {
        throw new IllegalArgumentException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, Object arg1, Object arg2, Object arg3, SimCallback callback) {
        throw new IllegalArgumentException("Wrong parameters passed in");
    }

}
