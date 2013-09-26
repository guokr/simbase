package com.guokr.simbase.util;

import java.util.Map;

public abstract class AbstractVectorSet {
    private Map<String, Object> config;
    private Basis               basis;

    public AbstractVectorSet(Map<String, Object> config, Basis basis) {
        this.config = config;
        this.basis = basis;
    }

    public Basis getBasis() {
        return basis;
    }
}
