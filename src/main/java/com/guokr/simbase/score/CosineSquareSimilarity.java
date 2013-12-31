package com.guokr.simbase.score;

import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.HashMap;
import java.util.Map;

import com.guokr.simbase.SimScore;

public class CosineSquareSimilarity implements SimScore {

    private static String                    name         = "cosinesq";
    private static Map<String, TIntFloatMap> denseCaches  = new HashMap<String, TIntFloatMap>();
    private static Map<String, TIntIntMap>   sparseCaches = new HashMap<String, TIntIntMap>();

    private String                           batchKey     = null;
    private int                              batchId      = -1;
    private boolean                          consumed     = false;

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
    public void beginBatch(String vkey, int vecId) {
        this.batchKey = vkey;
        this.batchId = vecId;
    }

    @Override
    public void endBatch() {
        this.batchKey = null;
        this.batchId = -1;
        this.consumed = false;
    }

    @Override
    public float score(String srcVKey, int srcId, float[] source, String tgtVKey, int tgtId, float[] target) {
        TIntFloatMap sourceCache = denseCaches.get(srcVKey);
        if (sourceCache == null) {
            sourceCache = new TIntFloatHashMap();
            denseCaches.put(srcVKey, sourceCache);
        }

        TIntFloatMap targetCache = denseCaches.get(tgtVKey);
        if (targetCache == null) {
            targetCache = new TIntFloatHashMap();
            denseCaches.put(tgtVKey, targetCache);
        }

        if (!consumed && batchKey.equals(srcVKey) && batchId == srcId) {
            sourceCache.put(srcId, flengthsq(source));
            consumed = true;
        }

        if (!consumed && batchKey.equals(tgtVKey) && batchId == tgtId) {
            targetCache.put(tgtId, flengthsq(target));
            consumed = true;
        }

        if (!sourceCache.containsKey(srcId)) {
            sourceCache.put(srcId, flengthsq(source));
        }

        if (!targetCache.containsKey(tgtId)) {
            targetCache.put(tgtId, flengthsq(target));
        }

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
        if (sourceCache == null) {
            sourceCache = new TIntIntHashMap();
            sparseCaches.put(srcVKey, sourceCache);
        }

        TIntIntMap targetCache = sparseCaches.get(tgtVKey);
        if (targetCache == null) {
            targetCache = new TIntIntHashMap();
            sparseCaches.put(tgtVKey, targetCache);
        }

        if (!consumed && batchKey.equals(srcVKey) && batchId == srcId) {
            sourceCache.put(srcId, ilengthsq(source));
            consumed = true;
        }

        if (!consumed && batchKey.equals(tgtVKey) && batchId == tgtId) {
            targetCache.put(tgtId, ilengthsq(target));
            consumed = true;
        }

        if (!sourceCache.containsKey(srcId)) {
            sourceCache.put(srcId, ilengthsq(source));
        }

        if (!targetCache.containsKey(tgtId)) {
            targetCache.put(tgtId, ilengthsq(target));
        }

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

}
