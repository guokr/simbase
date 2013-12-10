package com.guokr.simbase.store;

import com.guokr.simbase.events.RecommendationListener;
import com.guokr.simbase.events.VectorSetListener;

public class Recommendation implements VectorSetListener {
    private String func;
    private String order;
    private int limit;

    public Recommendation() {
        this.func = "cos";
        this.order = "desc";
        this.limit = 20;
    }

    public Recommendation(String func, String order, int limits) {
        this.func = func;
        this.order = order;
        this.limit = limits;
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
    public void onVectorAdded(int vecid, float[] inputed) {
    }

    @Override
    public void onVectorSetted(int vecid, float[] old, float[] inputed) {
    }

    @Override
    public void onVectorAccumulated(int vecid, float[] inputed, float[] accumulated) {
    }

    @Override
    public void onVectorRemoved(int vecid) {
    }

    public void addListener(RecommendationListener listener) {
    }
}
