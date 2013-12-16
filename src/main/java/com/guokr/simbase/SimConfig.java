package com.guokr.simbase;

import java.util.Map;

public class SimConfig extends SimContext {

    private static final long serialVersionUID = 25278573523513969L;

    @SuppressWarnings("unchecked")
    public SimConfig(Map<String, Object> raw) {
        super(raw);
        this.defaults = new SimContext((Map<String, Object>) raw.get("defaults"));
    }

    public void load(String file) {
    }

    public void dump(String file) {
    }

}
