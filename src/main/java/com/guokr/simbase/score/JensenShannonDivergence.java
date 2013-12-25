package com.guokr.simbase.score;

import com.guokr.simbase.SimScore;

public class JensenShannonDivergence implements SimScore {

    @Override
    public SortOrder order() {
        return SortOrder.Asc;
    }

    @Override
    public void beginBatch(String vkey, int vecId) {
    }

    @Override
    public void endBatch() {
    }

    @Override
    public float score(String srcVKey, int srcId, float[] source, String tgtVKey, int tgtId, float[] target) {
        return 0;
    }

    @Override
    public float score(String srcVKey, int srcId, int[] source, String tgtVKey, int tgtId, int[] target) {
        return 0;
    }

}
