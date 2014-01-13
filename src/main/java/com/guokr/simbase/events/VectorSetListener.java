package com.guokr.simbase.events;

import com.guokr.simbase.store.VectorSet;

public interface VectorSetListener {

    public void onVectorAdded(VectorSet evtSrc, int vecid, float[] vector);

    public void onVectorAdded(VectorSet evtSrc, int vecid, int[] vector);

    public void onVectorSetted(VectorSet evtSrc, int vecid, float[] old, float[] vector);

    public void onVectorSetted(VectorSet evtSrc, int vecid, int[] old, int[] vector);

    public void onVectorAccumulated(VectorSet evtSrc, int vecid, float[] vector, float[] accumulated);

    public void onVectorAccumulated(VectorSet evtSrc, int vecid, int[] vector, int[] accumulated);

    public void onVectorRemoved(VectorSet evtSrc, int vecid);

}
