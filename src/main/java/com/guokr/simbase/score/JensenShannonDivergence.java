package com.guokr.simbase.score;

import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.hash.TIntFloatHashMap;

import java.util.HashMap;
import java.util.Map;

import com.guokr.simbase.SimScore;
import com.guokr.simbase.store.VectorSet;

public class JensenShannonDivergence implements SimScore {

    private static final String              name   = "jensenshannon";
    private static Map<String, TIntFloatMap> caches = new HashMap<String, TIntFloatMap>();

    private static final float               ratio  = (float) Math.log(2);

    private static float lb(float val) {
        if (val > 0f) {
            float result = ((float) Math.log(val)) / ratio;
            return result;
        } else {
            return 0f;
        }
    }

    private static float finfo(float[] prob) {
        float info = 0f;
        for (float p : prob) {
            info += p * lb(p);
        }
        return info;
    }

    private static float iinfo(int[] freq) {
        float info = 0f;
        int len = freq.length;
        for (int i = 0; i < len; i += 2) {
            int p = freq[i + 1];
            info += p * lb(p);
        }
        return info;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public SortOrder order() {
        return SortOrder.Asc;
    }

    @Override
    public float score(String srcVKey, int srcId, float[] source, String tgtVKey, int tgtId, float[] target) {
        TIntFloatMap sourceCache = caches.get(srcVKey);
        TIntFloatMap targetCache = caches.get(tgtVKey);

        float scoring = 0f;
        int len = source.length;
        for (int i = 0; i < len; i++) {
            float p = source[i];
            float q = target[i];
            float m = (p + q) / 2;
            scoring += (-m * lb(m));
        }
        scoring += sourceCache.get(srcId) / 2f + targetCache.get(tgtId) / 2f;

        return scoring;
    }

    @Override
    public float score(String srcVKey, int srcId, int[] source, String tgtVKey, int tgtId, int[] target) {
        TIntFloatMap sourceCache = caches.get(srcVKey);
        TIntFloatMap targetCache = caches.get(tgtVKey);

        float scoring = 0f;
        int len1 = source.length;
        int len2 = target.length;
        int idx1 = 0, idx2 = 0;
        while (idx1 < len1 && idx2 < len2) {
            if (source[idx1] == target[idx2]) {
                float p = source[idx1 + 1];
                float q = target[idx2 + 1];
                float m = (p + q) / 2;
                scoring += (-m * lb(m));
                idx1 += 2;
                idx2 += 2;
            } else if (source[idx1] < target[idx2]) {
                float p = source[idx1 + 1];
                float m = p / 2;
                scoring += (-m * lb(m));
                idx1 += 2;
            } else {
                float q = target[idx2 + 1];
                float m = q / 2;
                scoring += (-m * lb(m));
                idx2 += 2;
            }
        }
        float srcScore = sourceCache.get(srcId);
        float tgtScore = targetCache.get(tgtId);
        scoring += srcScore / 2f + tgtScore / 2f;

        return scoring;
    }

    public void onAttached(String vkey) {
        caches.put(vkey, new TIntFloatHashMap());
    }

    public void onUpdated(String vkey, int vid, float[] vector) {
        caches.get(vkey).put(vid, finfo(vector));
    }

    public void onUpdated(String vkey, int vid, int[] vector) {
        caches.get(vkey).put(vid, iinfo(vector));
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
