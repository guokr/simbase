package com.guokr.simbase.events;

public interface VectorSetListener {

    public void onVectorAdded(int vecid, float[] inputed);

    public void onVectorSetted(int vecid, float[] old, float[] inputed);

    public void onVectorAccumulated(int vecid, float[] inputed, float[] accumulated);

    public void onVectorRemoved(int vecid);

}
