package com.guokr.simbase.engine;

import java.util.HashMap;

public class SimContext extends HashMap<String, Object> {

    private static final long serialVersionUID = -8288998975274604087L;

    public int getInt(String... keys) {
        return 0;
    }

    public int[] getIntArray(String... keys) {
        return null;
    }

    public double getFloat(String... keys) {
        return 0.0;
    }

    public double[] getFloatArray(String... keys) {
        return new double[0];
    }

    public String getString(String... keys) {
        return null;
    }

    public String getStringArray(String... keys) {
        return null;
    }

    public SimContext getSub(String... keys) {
        return null;
    }

    public Object get(String... keys) {
        return null;
    }

    public void set(Object val, String... keys) {
    }

}
