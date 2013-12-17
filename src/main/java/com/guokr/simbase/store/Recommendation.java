package com.guokr.simbase.store;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import com.guokr.simbase.events.RecommendationListener;
import com.guokr.simbase.events.VectorSetListener;

public class Recommendation implements VectorSetListener {

    public VectorSet                     source;
    public VectorSet                     target;

    private int                          limit;

    private TIntObjectMap<Sorter>        sorters;
    private List<RecommendationListener> listeners;

    public Recommendation(VectorSet source, VectorSet target) {
        this(source, target, 20);
    }

    public Recommendation(VectorSet source, VectorSet target, int limits) {
        this.source = source;
        this.target = target;
        this.limit = limits;
        this.sorters = new TIntObjectHashMap<Sorter>();
        this.listeners = new ArrayList<RecommendationListener>();
    }

    private float length(float[] vector) {
        float result = 0;
        for (float cmpn : vector) {
            result += cmpn * cmpn;
        }
        return (float) Math.sqrt(result);
    }

    public Sorter create(int vecid) {
        Sorter sorter = new Sorter(this.limit);
        this.sorters.put(vecid, sorter);
        return sorter;
    }

    public String get(int vecid) {
        return null;
    }

    public int[] rec(int vecid) {
        return this.sorters.get(vecid).vecids();
    }

    public void addListener(RecommendationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onVectorAdded(VectorSet evtSrc, int vecid, float[] inputed) {
        if (evtSrc == this.source) {
            evtSrc.score(vecid, length(inputed), inputed, this);
        } else if (evtSrc == this.target) {
        }
    }

    @Override
    public void onVectorSetted(VectorSet evtSrc, int vecid, float[] old, float[] inputed) {
        if (evtSrc == this.source) {
            evtSrc.score(vecid, length(inputed), inputed, this);
        } else if (evtSrc == this.target) {
        }
    }

    @Override
    public void onVectorAccumulated(VectorSet evtSrc, int vecid, float[] inputed, float[] accumulated) {
        if (evtSrc == this.source) {
            evtSrc.score(vecid, length(inputed), inputed, this);
        } else if (evtSrc == this.target) {
        }
    }

    @Override
    public void onVectorRemoved(VectorSet evtSrc, int vecid) {
        this.sorters.remove(vecid);
    }
}
