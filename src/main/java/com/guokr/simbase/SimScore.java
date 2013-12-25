package com.guokr.simbase;

public interface SimScore {

    public enum SortOrder {
        Asc, Desc
    }

    public SortOrder order();

    public void beginBatch(String vkey, int vecId);

    public void endBatch();

    public float score(String srcVKey, int srcId, float[] source, String tgtVKey, int tgtId, float[] target);

    public float score(String srcVKey, int srcId, int[] source, String tgtVKey, int tgtId, int[] target);

}
