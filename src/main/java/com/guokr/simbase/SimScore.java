package com.guokr.simbase;

import com.guokr.simbase.events.VectorSetListener;

public interface SimScore extends VectorSetListener {

    public enum SortOrder {
        Asc, Desc
    }

    public String name();

    public SortOrder order();

    public float score(String srcVKey, int srcId, float[] source, String tgtVKey, int tgtId, float[] target);

    public float score(String srcVKey, int srcId, int[] source, int tgt, String tgtVKey, int tgtId, int[] target,
            int tgtlen);

    public void onAttached(String vkey);

}
