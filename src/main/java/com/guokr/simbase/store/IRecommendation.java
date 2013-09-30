package com.guokr.simbase.store;

public interface IRecommendation {

    /*
     * add an vector with id and score
     */
    public void add(int vecid, float score);

    /*
     * the ids of vectors which is with the nth-largest score
     */
    public int[] ids(int vecid);

    /*
     * the id-score pairs in json format
     */
    public String jsonize(int vecid);

}
