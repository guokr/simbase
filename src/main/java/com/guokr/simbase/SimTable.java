package com.guokr.simbase;

import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class SimTable {

	public static SortedSet<Map.Entry<Integer, Float>> entriesSortedByValues(
			Map<Integer, Float> map) {
		SortedSet<Map.Entry<Integer, Float>> sortedEntries = new TreeSet<Map.Entry<Integer, Float>>(
				new Comparator<Map.Entry<Integer, Float>>() {
					@Override
					public int compare(Map.Entry<Integer, Float> e1, Map.Entry<Integer, Float> e2) {
						return (int) Math.signum(e2.getValue() - e1.getValue());
					}
				});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	private int maxlimit = 20;
	private TFloatList probs = new TFloatArrayList();
	private TIntIntMap indexer = new TIntIntHashMap();
	private TIntObjectHashMap<SortedMap<Integer, Float>> scores = new TIntObjectHashMap<SortedMap<Integer, Float>>();

	private void addScore(int src, int tgt, float score) {
		SortedMap<Integer, Float> range = scores.get(src);
		if (range == null) {
			range = new TreeMap<Integer, Float>();
			scores.put(src, range);
		}
		if (src != tgt) {
			range.put(tgt, score);
		}
		while (range.size() > maxlimit) {
			SortedSet<Map.Entry<Integer, Float>> entries = entriesSortedByValues(range);
			range.remove(entries.last().getKey());
		}
	}

	public void add(int docid, float[] distr) {
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
		add(docid, distr);
	}

	public void delete(int docid) {
		scores.remove(docid);
		int base = probs.indexOf((float) (docid + 1));
		while (probs.get(base + 1) < 1) {
			probs.remove(base + 1);
		}
		probs.remove(base);
	}

	public SortedSet<Map.Entry<Integer, Float>> retrieve(int docid) {
		if (scores.containsKey(docid)) {
			return entriesSortedByValues(scores.get(docid));
		} else {
			return entriesSortedByValues(new TreeMap<Integer, Float>());
		}
	}

	public float similarity(int docid1, int docid2) {
		return scores.get(docid1).get(docid2);
	}

}
