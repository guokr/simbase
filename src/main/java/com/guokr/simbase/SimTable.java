package com.guokr.simbase;

import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.SortedMap;
import java.util.TreeMap;

public class SimTable {

	private int maxlimit = 20;
	private TFloatList probs = new TFloatArrayList();
	private TIntObjectHashMap<SortedMap<Integer, Float>> scores = new TIntObjectHashMap<SortedMap<Integer, Float>>();

	private void addScore(int src, int tgt, float score) {
		SortedMap<Integer, Float> range = scores.get(src);
		if (range == null) {
			range = new TreeMap<Integer, Float>();
			scores.put(src, range);
		}
		range.put(tgt, score);
		while (range.size() > maxlimit) {
			range.remove(range.firstKey());
		}
	}

	public void add(int docid, float[] distr) {
		float length = 0;
		int start = probs.size();
		for (float val : distr) {
			probs.add(val);
			length += val * val;
		}
		probs.add((float) (docid + 1));
		probs.add(length);
		int end = probs.size();

		float score = 0;
		int base = 0;
		for (int offset = 0; offset < end; offset++) {
			float val = probs.get(offset);
			if (val < 1) {
				int idx = offset - base;
				if (idx < end - start - 1) {
					float another = distr[idx];
					score += another * val;
				}
			} else {
				float cosine = score * score / length / probs.get(offset + 1);
				addScore(docid, (int) val - 1, cosine);
				addScore((int) val - 1, docid, cosine);
				score = 0;
				offset = offset + 1;
				base = offset + 1;
			}
		}
	}
	
	public void update(int docid, float[] distr) {
		
	}

	public void delete(int docid) {
		scores.remove(docid);
		int base = probs.indexOf((float) (docid + 1));
		while (probs.get(base + 1) < 1) {
			probs.remove(base + 1);
		}
		probs.remove(base);
	}

	public SortedMap<Integer, Float> retrieve(int docid) {
		return scores.get(docid);
	}

	public float similarity(int docid1, int docid2) {
		return scores.get(docid1).get(docid2);
	}

}
