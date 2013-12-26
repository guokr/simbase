package com.guokr.simbase.store;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import com.guokr.simbase.SimScore;
import com.guokr.simbase.events.RecommendationListener;
import com.guokr.simbase.events.VectorSetListener;
import com.guokr.simbase.score.CosineSquareSimilarity;

public class Recommendation implements VectorSetListener {

    public VectorSet                     source;
    public VectorSet                     target;
    public SimScore                      scoring;

    private int                          limit;

    private TIntObjectMap<Sorter>        sorters;
    private TIntObjectHashMap<TIntList>  reverseIndexer = new TIntObjectHashMap<TIntList>();
    private List<RecommendationListener> listeners;

    public Recommendation(VectorSet source, VectorSet target) {
        this(source, target, new CosineSquareSimilarity(), 20);
    }

    public Recommendation(VectorSet source, VectorSet target, SimScore scoring) {
        this(source, target, scoring, 20);
    }

    public Recommendation(VectorSet source, VectorSet target, SimScore scoring, int limits) {
        this.source = source;
        this.target = target;
        this.limit = limits;
        this.scoring = scoring;
        this.sorters = new TIntObjectHashMap<Sorter>();
        this.listeners = new ArrayList<RecommendationListener>();
    }

    private void processDenseChangedEvt(VectorSet evtSrc, int vecid, float[] vector) {
        if (evtSrc == this.source) {
            scoring.beginBatch(source.key(), vecid);
            target.rescore(source.key(), vecid, vector, this);
            scoring.endBatch();
        } else if (evtSrc == this.target) {
            int tgtVecId = vecid;
            TIntObjectIterator<Sorter> iter = sorters.iterator();
            scoring.beginBatch(target.key(), tgtVecId);
            while (iter.hasNext()) {
                iter.advance();
                int srcVecId = iter.key();
                float score = scoring.score(source.key(), srcVecId, source.get(srcVecId), target.key(), tgtVecId, vector);
                add(srcVecId, tgtVecId, score);
            }
            scoring.endBatch();
        }
    }

    private void processSparseChangedEvt(VectorSet evtSrc, int vecid, int[] vector) {
        if (evtSrc == this.source) {
            scoring.beginBatch(source.key(), vecid);
            target.rescore(source.key(), vecid, vector, this);
            scoring.endBatch();
        } else if (evtSrc == this.target) {
            int tgtVecId = vecid;
            TIntObjectIterator<Sorter> iter = sorters.iterator();
            scoring.beginBatch(target.key(), tgtVecId);
            while (iter.hasNext()) {
                iter.advance();
                int srcVecId = iter.key();
                float score = scoring.score(source.key(), srcVecId, source._get(srcVecId), target.key(), tgtVecId, vector);
                add(srcVecId, tgtVecId, score);
            }
            scoring.endBatch();
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
        Sorter sorter = new Sorter(scoring.order(), this.limit);
        this.sorters.put(srcVecId, sorter);
        return sorter;
    }

    public void add(int srcVecId, int tgtVecId, float score) {
        if (!sorters.containsKey(srcVecId)) {
            create(srcVecId);
        }
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
        if (this.sorters.containsKey(vecid)) {
            return this.sorters.get(vecid).pickle();
        } else
            return new String[0];

    }

    public int[] rec(int vecid) {
        if (this.sorters.containsKey(vecid)) {
            return this.sorters.get(vecid).vecids();
        } else
            return new int[0];
    }

    public void addListener(RecommendationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onVectorAdded(VectorSet evtSrc, int vecid, float[] inputed) {
        processDenseChangedEvt(evtSrc, vecid, inputed);
    }

    @Override
    public void onVectorAdded(VectorSet evtSrc, int vecid, int[] vector) {
        processSparseChangedEvt(evtSrc, vecid, vector);
    }

    @Override
    public void onVectorSetted(VectorSet evtSrc, int vecid, float[] old, float[] vector) {
        processDenseChangedEvt(evtSrc, vecid, vector);
    }

    @Override
    public void onVectorSetted(VectorSet evtSrc, int vecid, int[] old, int[] vector) {
        processSparseChangedEvt(evtSrc, vecid, vector);
    }

    @Override
    public void onVectorAccumulated(VectorSet evtSrc, int vecid, float[] vector, float[] accumulated) {
        processDenseChangedEvt(evtSrc, vecid, vector);
    }

    @Override
    public void onVectorAccumulated(VectorSet evtSrc, int vecid, int[] vector, int[] accumulated) {
        processSparseChangedEvt(evtSrc, vecid, vector);
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
