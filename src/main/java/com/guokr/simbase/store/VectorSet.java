package com.guokr.simbase.store;

public interface VectorSet {

    public void remove(int vecid);

    public float[] get(int vecid);

    public void add(int vecid, float[] distr);

    public void set(int vecid, float[] distr);

    public void accumulate(int vecid, float[] distr);

    public int[] _get(int vecid);

    void _add(int vecid, int[] pairs);

    void _set(int vecid, int[] pairs);

    void _accumulate(int vecid, int[] pairs);

}
