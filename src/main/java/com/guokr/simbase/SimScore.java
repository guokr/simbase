package com.guokr.simbase;

import com.guokr.simbase.events.VectorSetListener;

public interface SimScore extends VectorSetListener {

    public enum SortOrder {
        Asc, Desc
    }

    public String name();

    public SortOrder order();

    public float score(String srcVKey, long srcId, float[] source, String tgtVKey, long tgtId, float[] target);

    public float score(String srcVKey, long srcId, int[] source, int srclen, String tgtVKey, long tgtId, int[] target,
            int tgtlen);

    public void onAttached(String vkey);

}
