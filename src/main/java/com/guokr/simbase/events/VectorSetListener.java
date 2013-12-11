package com.guokr.simbase.events;

import com.guokr.simbase.store.VectorSet;

public interface VectorSetListener {

    public void onVectorAdded(VectorSet evtSrc, int vecid, float[] inputed);

    public void onVectorSetted(VectorSet evtSrc, int vecid, float[] old, float[] inputed);

    public void onVectorAccumulated(VectorSet evtSrc, int vecid, float[] inputed, float[] accumulated);

    public void onVectorRemoved(VectorSet evtSrc, int vecid);

}
