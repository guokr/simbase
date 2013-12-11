package com.guokr.simbase.events;

import com.guokr.simbase.store.VectorSet;

public interface VectorSetListener {

    public void onVectorAdded(VectorSet source, int vecid, float[] inputed);

    public void onVectorSetted(VectorSet source, int vecid, float[] old, float[] inputed);

    public void onVectorAccumulated(VectorSet source, int vecid, float[] inputed, float[] accumulated);

    public void onVectorRemoved(VectorSet source, int vecid);

}
