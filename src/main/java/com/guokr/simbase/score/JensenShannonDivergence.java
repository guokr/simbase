package com.guokr.simbase.score;

import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.HashMap;
import java.util.Map;

import com.guokr.simbase.SimScore;

public class JensenShannonDivergence implements SimScore {

	private static final String name = "jensenshannon";
	private static Map<String, TIntFloatMap> denseCaches = new HashMap<String, TIntFloatMap>();
	private static Map<String, TIntIntMap> sparseCaches = new HashMap<String, TIntIntMap>();

	private static final float ratio = (float) Math.log(2);

	private String batchKey = null;
	private int batchId = -1;
	private boolean consumed = false;

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

	private static int iinfo(int[] freq) {
		float info = 0f;
		int len = freq.length;
		for (int i = 0; i < len; i += 2) {
			int p = freq[i + 1];
			info += p * lb(p);
		}
		return Math.round(info);
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
	public float score(String srcVKey, int srcId, float[] source,
			String tgtVKey, int tgtId, float[] target) {
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
			sourceCache.put(srcId, finfo(source));
			consumed = true;
		}

		if (!consumed && batchKey.equals(tgtVKey) && batchId == tgtId) {
			targetCache.put(tgtId, finfo(target));
			consumed = true;
		}

		if (!sourceCache.containsKey(srcId)) {
			sourceCache.put(srcId, finfo(source));
		}

		if (!targetCache.containsKey(tgtId)) {
			targetCache.put(tgtId, finfo(target));
		}

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
	public float score(String srcVKey, int srcId, int[] source, String tgtVKey,
			int tgtId, int[] target) {
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
			sourceCache.put(srcId, iinfo(source));
			consumed = true;
		}

		if (!consumed && batchKey.equals(tgtVKey) && batchId == tgtId) {
			targetCache.put(tgtId, iinfo(target));
			consumed = true;
		}

		if (!sourceCache.containsKey(srcId)) {
			sourceCache.put(srcId, iinfo(source));
		}

		if (!targetCache.containsKey(tgtId)) {
			targetCache.put(tgtId, iinfo(target));
		}

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

}
