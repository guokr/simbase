package com.guokr.simbase;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Map;
import java.util.SortedSet;

public class SimCluster {

	private TIntObjectHashMap centers = new TIntObjectHashMap();

	public int add(int docid, float[] distr) {
		return 0;
	}

	public int update(int docid, float[] distr) {
		return 0;
	}

	public int delete(int docid) {
		return 0;
	}

	public SortedSet<Map.Entry<Integer, Float>> retrieve(int docid) {
		return null;
	}

}
