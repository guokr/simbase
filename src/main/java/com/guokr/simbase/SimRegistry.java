package com.guokr.simbase;

import java.util.HashMap;
import java.util.Map;

import clojure.lang.IFn;

public class SimRegistry {

    private Map<String, IFn> registry = new HashMap<String, IFn>();

    public void add(String key, IFn fn) {
        registry.put(key, fn);
    }

    public IFn get(String key) {
        return registry.get(key);
    }

}
