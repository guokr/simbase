package com.guokr.simbase.events;

import com.guokr.simbase.store.VectorSet;

public interface VectorSetListener {

    public void onVectorAdded(VectorSet evtSrc, long vecid, float[] vector);

    public void onVectorAdded(VectorSet evtSrc, long vecid, int[] vector);

    public void onVectorSetted(VectorSet evtSrc, long vecid, float[] old, float[] vector);

    public void onVectorSetted(VectorSet evtSrc, long vecid, int[] old, int[] vector);

    public void onVectorAccumulated(VectorSet evtSrc, long vecid, float[] vector, float[] accumulated);

    public void onVectorAccumulated(VectorSet evtSrc, long vecid, int[] vector, int[] accumulated);

    public void onVectorRemoved(VectorSet evtSrc, long vecid);

}
