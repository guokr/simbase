package com.guokr.simbase.store;

import com.guokr.simbase.SimContext;

public class Recommendation implements IRecommendation {
    private int limit;

    public Recommendation(SimContext simContext) {
        this.limit = simContext.getInt("limit");
    }

    @Override
    public void add(int vecid, float profile) {

    }

    @Override
    public int[] ids(int vecid) {
        return null;
    }

    @Override
    public String jsonize(int vecid) {
        return null;
    }
}
