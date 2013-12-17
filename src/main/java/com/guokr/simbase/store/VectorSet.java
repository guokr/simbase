package com.guokr.simbase.store;

import com.guokr.simbase.events.VectorSetListener;

public interface VectorSet {

    public void score(int vecid, float length, float[] vector, Recommendation rec);

    public void remove(int vecid);

    public float[] get(int vecid);

    public void add(int vecid, float[] vector);

    public void set(int vecid, float[] vector);

    public void accumulate(int vecid, float[] vector);

    public int[] _get(int vecid);

    void _add(int vecid, int[] pairs);

    void _set(int vecid, int[] pairs);

    void _accumulate(int vecid, int[] pairs);

    public void addListener(VectorSetListener listener);

}
