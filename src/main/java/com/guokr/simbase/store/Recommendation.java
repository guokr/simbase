package com.guokr.simbase.store;

import java.util.ArrayList;
import java.util.List;

import com.guokr.simbase.events.RecommendationListener;
import com.guokr.simbase.events.VectorSetListener;

public class Recommendation implements VectorSetListener {
    private String                       func;
    private String                       order;
    private int                          limit;
    private List<RecommendationListener> listeners;

    public Recommendation() {
        this("cos", "desc", 20);
    }

    public Recommendation(String func, String order, int limits) {
        this.func = func;
        this.order = order;
        this.limit = limits;
        this.listeners = new ArrayList<RecommendationListener>();
    }

    public void add(int vecid, float[] vector) {

    }

    public String get(int vecid) {
        return null;
    }

    public int[] rec(int vecid) {
        return null;
    }

    @Override
    public void onVectorAdded(VectorSet source, int vecid, float[] inputed) {
    }

    @Override
    public void onVectorSetted(VectorSet source, int vecid, float[] old, float[] inputed) {
    }

    @Override
    public void onVectorAccumulated(VectorSet source, int vecid, float[] inputed, float[] accumulated) {
    }

    @Override
    public void onVectorRemoved(VectorSet source, int vecid) {
    }

    public void addListener(RecommendationListener listener) {
        listeners.add(listener);
    }
}
