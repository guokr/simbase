package com.guokr.simbase.events;

public interface RecommendationListener {

    public void onItemAdded(int vecid, float score);

    public void onItemRemoved(int vecid, float score);

}
