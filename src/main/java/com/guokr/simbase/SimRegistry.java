package com.guokr.simbase;

import java.util.HashMap;
import java.util.Map;

public class SimRegistry {

    private Map<String, SimCommand> registry = new HashMap<String, SimCommand>();

    public void add(String key, SimCommand cmd) {
        registry.put(key, cmd);
    }

    public SimCommand get(String key) {
        return registry.get(key);
    }

}
