package com.guokr.simbase;

import java.util.HashMap;
import java.util.Map;

import com.guokr.simbase.errors.SimCommandException;

public class SimRegistry {

    private Map<String, SimCommand> registry = new HashMap<String, SimCommand>();

    public void add(String key, SimCommand cmd) {
        registry.put(key, cmd);
    }

    public SimCommand get(String key) {
        key = key.toLowerCase();
        SimCommand command = registry.get(key);
        if (command == null) {
            throw new SimCommandException(String.format("Unknown command '%s'", key));
        }
        return command;
    }
}
