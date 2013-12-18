package com.guokr.simbase.store;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import com.guokr.simbase.events.RecommendationListener;
import com.guokr.simbase.events.VectorSetListener;

public class Recommendation implements VectorSetListener {

	public VectorSet source;
	public VectorSet target;

	private int limit;

	private TIntObjectMap<Sorter> sorters;
	private TIntObjectHashMap<TIntList> reverseIndexer = new TIntObjectHashMap<TIntList>();
	private List<RecommendationListener> listeners;

	public Recommendation(VectorSet source, VectorSet target) {
		this(source, target, 20);
	}

	public Recommendation(VectorSet source, VectorSet target, int limits) {
		this.source = source;
		this.target = target;
		this.limit = limits;
		this.sorters = new TIntObjectHashMap<Sorter>();
		this.listeners = new ArrayList<RecommendationListener>();
	}

	private float length(float[] vector) {
		float result = 0;
		for (float cmpn : vector) {
			result += cmpn * cmpn;
		}
		return (float) Math.sqrt(result);
	}

	private float score(float[] vector1, float[] vector2) {
		float result = 0f, length1 = 0f, length2 = 0f;
		int len = vector1.length;
		for (int i = 0; i < len; i++) {
			length1 += vector1[i] * vector1[i];
			length2 += vector2[i] * vector2[i];
			result += vector1[i] * vector2[i];
		}
		return result / (float) Math.sqrt(length1) / (float) Math.sqrt(length2);
	}

	private void processChangedEvt(VectorSet evtSrc, int vecid, float[] inputed) {
		if (evtSrc == this.source) {
			target.rescore(vecid, length(inputed), inputed, this);
		} else if (evtSrc == this.target) {
			int tgtVecId = vecid;
			TIntObjectIterator<Sorter> iter = sorters.iterator();
			while (iter.hasNext()) {
				int srcVecId = iter.key();
				add(srcVecId, tgtVecId, score(source.get(srcVecId), inputed));
				iter.advance();
			}
		}
	}

	private void processDeletedEvt(int tgtVecId) {
		if (this.reverseIndexer.containsKey(tgtVecId)) {
			TIntList range = this.reverseIndexer.get(tgtVecId);
			TIntIterator iter = range.iterator();
			while(iter.hasNext()) {
				int srcVecId = iter.next();
				this.sorters.get(srcVecId).remove(tgtVecId);
			}
		}
	}

	public Sorter create(int srcVecId) {
		Sorter sorter = new Sorter(this.limit);
		this.sorters.put(srcVecId, sorter);
		return sorter;
	}

	public void add(int srcVecId, int tgtVecId, float score) {
		this.sorters.get(srcVecId).add(tgtVecId, score);
		if (!this.reverseIndexer.containsKey(tgtVecId)) {
			this.reverseIndexer.put(tgtVecId, new TIntArrayList());
		} else {
			TIntList range = this.reverseIndexer.get(tgtVecId);
			if (range.indexOf(srcVecId) == -1) {
				range.add(srcVecId);
			}
		}
	}

	public String[] get(int vecid) {
		return this.sorters.get(vecid).pickle();
	}

	public int[] rec(int vecid) {
		return this.sorters.get(vecid).vecids();
	}

	public void addListener(RecommendationListener listener) {
		listeners.add(listener);
	}

	@Override
	public void onVectorAdded(VectorSet evtSrc, int vecid, float[] inputed) {
		processChangedEvt(evtSrc, vecid, inputed);
	}

	@Override
	public void onVectorSetted(VectorSet evtSrc, int vecid, float[] old,
			float[] inputed) {
		processChangedEvt(evtSrc, vecid, inputed);
	}

	@Override
	public void onVectorAccumulated(VectorSet evtSrc, int vecid,
			float[] inputed, float[] accumulated) {
		processChangedEvt(evtSrc, vecid, inputed);
	}

	@Override
	public void onVectorRemoved(VectorSet evtSrc, int vecid) {
		if (evtSrc == this.source) {
			this.sorters.remove(vecid);
		} else if (evtSrc == this.target) {
			processDeletedEvt(vecid);
		}
	}

}
