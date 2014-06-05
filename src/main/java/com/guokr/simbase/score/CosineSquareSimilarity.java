package com.guokr.simbase.score;

import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.hash.TIntFloatHashMap;

import java.util.HashMap;
import java.util.Map;

import com.guokr.simbase.SimScore;
import com.guokr.simbase.store.VectorSet;

public class CosineSquareSimilarity implements SimScore {

    private static String                    name   = "cosinesq";
    private static Map<String, TIntFloatMap> caches = new HashMap<String, TIntFloatMap>();

    private float flengthsq(float[] vector) {
        float result = 0f;
        int len = vector.length;
        for (int i = 0; i < len; i++) {
            result += vector[i] * vector[i];
        }
        return result;
    }

    private float ilengthsq(int[] vector) {
        int result = 0;
        int len = vector.length;
        for (int i = 0; i < len;) {
            result += vector[i + 1] * vector[i + 1];
            i += 2;
        }
        return result;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public SortOrder order() {
        return SortOrder.Desc;
    }

    @Override
    public float score(String srcVKey, int srcId, float[] source, String tgtVKey, int tgtId, float[] target) {
        TIntFloatMap sourceCache = caches.get(srcVKey);
        TIntFloatMap targetCache = caches.get(tgtVKey);

        float scoring = 0;
        int len = source.length;
        for (int i = 0; i < len; i++) {
            scoring += source[i] * target[i];
        }

        scoring = scoring * scoring / sourceCache.get(srcId) / targetCache.get(tgtId);

        return scoring;
    }

    @Override
    public float score(String srcVKey, int srcId, int[] source, int srclen, String tgtVKey, int tgtId, int[] target,
            int tgtlen) {
        TIntFloatMap sourceCache = caches.get(srcVKey);
        TIntFloatMap targetCache = caches.get(tgtVKey);

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
                    scoring += source[idx1 + 1] * target[idx2 + 1];
                    idx1 += 2;
                    idx2 += 2;
                    if (idx1 >= srclen || idx2 >= tgtlen)
                        break;
                }
            }
        }

        scoring = scoring * scoring / sourceCache.get(srcId) / targetCache.get(tgtId);

        return scoring;
    }

    public void onAttached(String vkey) {
        caches.put(vkey, new TIntFloatHashMap());
    }

    public void onUpdated(String vkey, int vid, float[] vector) {
        caches.get(vkey).put(vid, flengthsq(vector));
    }

    public void onUpdated(String vkey, int vid, int[] vector) {
        caches.get(vkey).put(vid, ilengthsq(vector));
    }

    public void onRemoved(String vkey, int vid) {
        caches.get(vkey).remove(vid);
    }

    @Override
    public void onVectorAdded(VectorSet evtSrc, int vecid, float[] vector) {
        onUpdated(evtSrc.key(), vecid, vector);
    }

    @Override
    public void onVectorAdded(VectorSet evtSrc, int vecid, int[] vector) {
        onUpdated(evtSrc.key(), vecid, vector);
    }

    @Override
    public void onVectorSetted(VectorSet evtSrc, int vecid, float[] old, float[] vector) {
        onUpdated(evtSrc.key(), vecid, vector);
    }

    @Override
    public void onVectorSetted(VectorSet evtSrc, int vecid, int[] old, int[] vector) {
        onUpdated(evtSrc.key(), vecid, vector);
    }

    @Override
    public void onVectorAccumulated(VectorSet evtSrc, int vecid, float[] vector, float[] accumulated) {
        onUpdated(evtSrc.key(), vecid, accumulated);
    }

    @Override
    public void onVectorAccumulated(VectorSet evtSrc, int vecid, int[] vector, int[] accumulated) {
        onUpdated(evtSrc.key(), vecid, accumulated);
    }

    @Override
    public void onVectorRemoved(VectorSet evtSrc, int vecid) {
        onRemoved(evtSrc.key(), vecid);
    }

}
