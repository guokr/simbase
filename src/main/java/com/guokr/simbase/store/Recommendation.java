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

    public VectorSet                     source;
    public VectorSet                     target;

    private int                          limit;

    private TIntObjectMap<Sorter>        sorters;
    private TIntObjectHashMap<TIntList>  reverseIndexer = new TIntObjectHashMap<TIntList>();
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

    private int length(int[] pairs) {
        int result = 0;
        int len = pairs.length;
        for (int i = 0; i < len;) {
            result += pairs[i + 1] * pairs[i + 1];
            i += 2;
        }
        return result;
    }

    private float score(int[] pairs1, int[] pairs2) {
        float result = 0f;
        int len1 = pairs1.length;
        int len2 = pairs2.length;
        int idx1 = 0, idx2 = 0;
        while (idx1 < len1 && idx2 < len2) {
            if (pairs1[idx1] == pairs2[idx2]) {
                result += pairs1[idx1 + 1] * pairs2[idx2 + 1];
                idx1 += 2;
                idx2 += 2;
            } else if (pairs1[idx1] < pairs2[idx2]) {
                idx1 += 2;
            } else {
                idx2 += 2;
            }
        }
        return result * result / length(pairs1) / length(pairs2);
    }

    private void processChangedEvt(VectorSet evtSrc, int vecid, int[] inputed) {
        if (evtSrc == this.source) {
            target.rescore(vecid, length(inputed), inputed, this);
        }
        if (evtSrc == this.target) {
            int tgtVecId = vecid;
            TIntObjectIterator<Sorter> iter = sorters.iterator();
            while (iter.hasNext()) {
                iter.advance();
                int srcVecId = iter.key();
                if (!(this.source == this.target && srcVecId == tgtVecId)) {
                    add(srcVecId, tgtVecId, score(source._get(srcVecId), inputed));
                }
            }
        }
    }

    private void processDeletedEvt(int tgtVecId) {
        if (this.reverseIndexer.containsKey(tgtVecId)) {
            TIntList range = this.reverseIndexer.get(tgtVecId);
            TIntIterator iter = range.iterator();
            while (iter.hasNext()) {
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
    public void onVectorAdded(VectorSet evtSrc, int vecid, int[] inputed) {
        processChangedEvt(evtSrc, vecid, inputed);
    }

    @Override
    public void onVectorSetted(VectorSet evtSrc, int vecid, int[] old, int[] inputed) {
        processChangedEvt(evtSrc, vecid, inputed);
    }

    @Override
    public void onVectorAccumulated(VectorSet evtSrc, int vecid, int[] inputed, int[] accumulated) {
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
