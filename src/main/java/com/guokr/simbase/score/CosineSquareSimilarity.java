package com.guokr.simbase.score;

import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntFloatHashMap;

import java.util.HashMap;
import java.util.Map;

import com.guokr.simbase.SimScore;
import com.guokr.simbase.store.VectorSet;

public class CosineSquareSimilarity implements SimScore {

    private static String                    name         = "cosinesq";
    private static Map<String, TIntFloatMap> denseCaches  = new HashMap<String, TIntFloatMap>();
    private static Map<String, TIntIntMap>   sparseCaches = new HashMap<String, TIntIntMap>();

    private float flengthsq(float[] vector) {
        float result = 0f;
        int len = vector.length;
        for (int i = 0; i < len; i++) {
            result += vector[i] * vector[i];
        }
        return result;
    }

    private int ilengthsq(int[] vector) {
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
        TIntFloatMap sourceCache = denseCaches.get(srcVKey);
        TIntFloatMap targetCache = denseCaches.get(tgtVKey);

        float scoring = 0;
        int len = source.length;
        for (int i = 0; i < len; i++) {
            scoring += source[i] * target[i];
        }

        scoring = scoring * scoring / sourceCache.get(srcId) / targetCache.get(tgtId);

        return scoring;
    }

    @Override
    public float score(String srcVKey, int srcId, int[] source, String tgtVKey, int tgtId, int[] target) {
        TIntIntMap sourceCache = sparseCaches.get(srcVKey);
        TIntIntMap targetCache = sparseCaches.get(tgtVKey);

        float scoring = 0f;
        int len1 = source.length;
        int len2 = target.length;
        int idx1 = 0, idx2 = 0;
        while (idx1 < len1 && idx2 < len2) {
            if (source[idx1] == target[idx2]) {
                scoring += source[idx1 + 1] * target[idx2 + 1];
                idx1 += 2;
                idx2 += 2;
            } else if (source[idx1] < target[idx2]) {
                idx1 += 2;
            } else {
                idx2 += 2;
            }
        }

        scoring = scoring * scoring / sourceCache.get(srcId) / targetCache.get(tgtId);

        return scoring;
    }

    public void onAttached(String vkey) {
        denseCaches.put(vkey, new TIntFloatHashMap());
    }

    public void onUpdated(String vkey, int vid, float[] vector) {
        TIntFloatMap cache = denseCaches.get(vkey);
        cache.put(vid, flengthsq(vector));
    }

    public void onUpdated(String vkey, int vid, int[] vector) {
        TIntFloatMap cache = denseCaches.get(vkey);
        cache.put(vid, ilengthsq(vector));
    }

    public void onRemoved(String vkey, int vid) {
        TIntFloatMap denseCache = denseCaches.get(vkey);
        if (denseCache != null) {
            denseCache.remove(vid);
        }
        TIntIntMap sparseCache = sparseCaches.get(vkey);
        if (sparseCache != null) {
            sparseCache.remove(vid);
        }
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
