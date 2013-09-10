package com.guokr.simbase.util;

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class VectorSet {

	private TFloatList probs = new TFloatArrayList();
	private TIntIntMap indexer = new TIntIntHashMap();
	private TIntObjectHashMap<TIntList> reverseIndexer = new TIntObjectHashMap<TIntList>();

	public int put(int docid, float[] distr) {
		float length = 0;
		int start;
		if (indexer.containsKey(docid)) {
			start = indexer.get(docid);
			int cursor = start;
			for (float val : distr) {
				probs.set(cursor, val);
				length += val * val;
				cursor++;
			}
			probs.set(cursor++, (float) (docid + 1));
			probs.set(cursor, length);
		} else {
			start = probs.size();
			indexer.put(docid, start);
			for (float val : distr) {
				probs.add(val);
				length += val * val;
			}
			probs.add((float) (docid + 1));
			probs.add(length);
		}
		return start;
	}

}
