package com.guokr.simbase;

import com.guokr.simbase.errors.SimCommandException;

public abstract class SimCommand {

    public abstract String signature();

    public void invoke(SimEngine engine, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, String arg2, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, long arg2, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, float arg2, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, String[] arg2, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, int[] arg2, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, float[] arg2, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, String arg2, String arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, String arg2, long arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, String arg2, float arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, String arg2, String[] arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, String arg2, int[] arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, String arg2, float[] arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, long arg2, String arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, long arg2, long arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, long arg2, float arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, long arg2, String[] arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, long arg2, int[] arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, long arg2, float[] arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, float arg2, String arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, float arg2, long arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, float arg2, float arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, float arg2, String[] arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, float arg2, int[] arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, float arg2, float[] arg3, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, long arg2, String arg3, long arg4, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

    public void invoke(SimEngine engine, String arg1, long arg2, String arg3, long[] arg4, SimCallback callback) {
        throw new SimCommandException("Wrong parameters passed in");
    }

}
