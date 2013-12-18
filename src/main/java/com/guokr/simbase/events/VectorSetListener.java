package com.guokr.simbase.events;

import com.guokr.simbase.store.VectorSet;

public interface VectorSetListener {

    public void onVectorAdded(VectorSet evtSrc, int vecid, int[] pairs);

    public void onVectorSetted(VectorSet evtSrc, int vecid, int[] old, int[] pairs);

    public void onVectorAccumulated(VectorSet evtSrc, int vecid, int[] pairs, int[] accumulated);

    public void onVectorRemoved(VectorSet evtSrc, int vecid);

}
