package com.guokr.simbase.score;

import com.guokr.simbase.SimScore;
import com.guokr.simbase.store.VectorSet;

public class EuclideanDistanceSquare implements SimScore {

    private static String name = "euclideansq";

    @Override
    public String name() {
        return name;
    }

    @Override
    public SortOrder order() {
        return SortOrder.Asc;
    }

    @Override
    public float score(String srcVKey, long srcId, float[] source, String tgtVKey, long tgtId, float[] target) {

        float scoring = 0;
        int len = source.length;
        for (int i = 0; i < len; i++) {
            float d = source[i] - target[i];
            scoring += d * d;
        }

        return scoring;
    }

    @Override
    public float score(String srcVKey, long srcId, int[] source, int srclen, String tgtVKey, long tgtId, int[] target,
            int tgtlen) {

        float scoring = 0f;
        int idx1 = 0, idx2 = 0;
        if (idx1 < srclen && idx2 < tgtlen) {
            while (true) {
                if (source[idx1] < target[idx2]) {
                    idx1 += 2;
                    if (idx1 >= srclen)
                        break;
                } else if (source[idx1] > target[idx2]) {
                    idx2 += 2;
                    if (idx2 >= tgtlen)
                        break;
                } else {
                    int d = source[idx1 + 1] - target[idx2 + 1];
                    scoring += d * d;
                    idx1 += 2;
                    idx2 += 2;
                    if (idx1 >= srclen || idx2 >= tgtlen)
                        break;
                }
            }
        }

        return scoring;
    }

    public void onAttached(String vkey) {
    }

    public void onUpdated(String vkey, long vecid, float[] vector) {
    }

    public void onUpdated(String vkey, long vecid, int[] vector) {
    }

    public void onRemoved(String vkey, long vecid) {
    }

    @Override
    public void onVectorAdded(VectorSet evtSrc, long vecid, float[] vector) {
        onUpdated(evtSrc.key(), vecid, vector);
    }

    @Override
    public void onVectorAdded(VectorSet evtSrc, long vecid, int[] vector) {
        onUpdated(evtSrc.key(), vecid, vector);
    }

    @Override
    public void onVectorSetted(VectorSet evtSrc, long vecid, float[] old, float[] vector) {
        onUpdated(evtSrc.key(), vecid, vector);
    }

    @Override
    public void onVectorSetted(VectorSet evtSrc, long vecid, int[] old, int[] vector) {
        onUpdated(evtSrc.key(), vecid, vector);
    }

    @Override
    public void onVectorAccumulated(VectorSet evtSrc, long vecid, float[] vector, float[] accumulated) {
        onUpdated(evtSrc.key(), vecid, accumulated);
    }

    @Override
    public void onVectorAccumulated(VectorSet evtSrc, long vecid, int[] vector, int[] accumulated) {
        onUpdated(evtSrc.key(), vecid, accumulated);
    }

    @Override
    public void onVectorRemoved(VectorSet evtSrc, long vecid) {
        onRemoved(evtSrc.key(), vecid);
    }

}
