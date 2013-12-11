package com.guokr.simbase.store;

import java.util.ArrayList;
import java.util.List;

import com.guokr.simbase.events.RecommendationListener;
import com.guokr.simbase.events.VectorSetListener;

public class Recommendation implements VectorSetListener {

    public VectorSet                     source;
    public VectorSet                     target;

    private String                       func;
    private String                       order;
    private int                          limit;

    private List<RecommendationListener> listeners;

    public Recommendation(VectorSet source, VectorSet target) {
        this(source, target, "cos", "desc", 20);
    }

    public Recommendation(VectorSet source, VectorSet target, String func, String order, int limits) {
        this.source = source;
        this.target = target;
        this.func = func;
        this.order = order;
        this.limit = limits;
        this.listeners = new ArrayList<RecommendationListener>();
    }

    public String get(int vecid) {
        return null;
    }

    public int[] rec(int vecid) {
        return null;
    }

    public void addListener(RecommendationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onVectorAdded(VectorSet evtSrc, int vecid, float[] inputed) {
    }

    @Override
    public void onVectorSetted(VectorSet evtSrc, int vecid, float[] old, float[] inputed) {
    }

    @Override
    public void onVectorAccumulated(VectorSet evtSrc, int vecid, float[] inputed, float[] accumulated) {
    }

    @Override
    public void onVectorRemoved(VectorSet evtSrc, int vecid) {
    }
}
