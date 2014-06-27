package com.guokr.simbase.events;

public interface RecommendationListener {

    public void onItemAdded(long vecid, float score);

    public void onItemRemoved(long vecid, float score);

}
