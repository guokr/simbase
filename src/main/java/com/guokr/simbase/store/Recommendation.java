package com.guokr.simbase.store;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guokr.simbase.SimScore;
import com.guokr.simbase.events.RecommendationListener;
import com.guokr.simbase.events.VectorSetListener;
import com.guokr.simbase.score.CosineSquareSimilarity;

public class Recommendation implements VectorSetListener {

    private static final Logger          logger         = LoggerFactory.getLogger(Recommendation.class);

    public VectorSet                     source;
    public VectorSet                     target;
    public SimScore                      scoring;

    int                                  limit;

    TLongList                            sorterKeys     = new TLongArrayList();
    TLongObjectMap<Sorter>               sorters        = new TLongObjectHashMap<Sorter>();

    private TLongObjectMap<TLongSet>     reverseIndexer = new TLongObjectHashMap<TLongSet>();
    private List<RecommendationListener> listeners      = new ArrayList<RecommendationListener>();

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

        scoring.onAttached(source.key());
        source.addListener(scoring);

        if (source.type().equals("dense")) {
            for (long id : source.ids()) {
                scoring.onVectorAdded(source, id, source.get(id));
            }
        } else {
            for (long id : source.ids()) {
                scoring.onVectorAdded(source, id, source._get(id));
            }
        }

        if (source != target) {
            scoring.onAttached(target.key());
            target.addListener(scoring);

            if (target.type().equals("dense")) {
                for (long id : target.ids()) {
                    scoring.onVectorAdded(target, id, target.get(id));
                }
            } else {
                for (long id : target.ids()) {
                    scoring.onVectorAdded(target, id, target._get(id));
                }
            }
        }
    }

    public void clean() {
        TLongObjectMap<Sorter> oldSorters = sorters;
        TLongList oldSorterKeys = sorterKeys;
        sorterKeys = new TLongArrayList();
        sorters = new TLongObjectHashMap<Sorter>();
        reverseIndexer = new TLongObjectHashMap<TLongSet>();

        TLongIterator iter = oldSorterKeys.iterator();
        while (iter.hasNext()) {
            long srcId = iter.next();
            if (this.source.contains(srcId)) {
                sorterKeys.add(srcId);
                Sorter oldSorter = oldSorters.get(srcId);
                Sorter sorter = new Sorter(this, srcId, oldSorter.order, oldSorter.limits);
                sorters.put(srcId, sorter);
                for (long tgtId : oldSorter.vecids()) {
                    if (this.target.contains(tgtId)) {
                        sorter.add(tgtId, oldSorter.get(tgtId));
                        if (!reverseIndexer.containsKey(tgtId)) {
                            reverseIndexer.put(tgtId, new TLongHashSet());
                        }
                        reverseIndexer.get(tgtId).add(srcId);
                    } else {
                        logger.warn(String.format("target vector[%d] not in sorter[%d] of rec[%s][%s]", tgtId, srcId,
                                this.source.key(), this.target.key()));
                    }
                }
            } else {
                logger.warn(String.format("source vector[%d] not in rec[%s][%s]", srcId, this.source.key(),
                        this.target.key()));
            }
        }
    }

    public void addReverseIndex(long srcVecId, long tgtVecId) {
        if (!this.reverseIndexer.containsKey(tgtVecId)) {
            this.reverseIndexer.put(tgtVecId, new TLongHashSet());
        }
        reverseIndexer.get(tgtVecId).add(srcVecId);
    }

    public void deleteReverseIndex(long srcVecId, long tgtVecId) {
        reverseIndexer.get(tgtVecId).remove(srcVecId);
    }

    private void processDenseChangedEvt(VectorSet evtSrc, long vecid, float[] vector) {
        if (evtSrc == this.source) {
            target.rescore(source.key(), vecid, vector, this);
        } else if (evtSrc == this.target) {
            long tgtVecId = vecid;
            TLongIterator iter = sorterKeys.iterator();
            while (iter.hasNext()) {
                long srcVecId = iter.next();
                float score = scoring.score(source.key(), srcVecId, source.get(srcVecId), target.key(), tgtVecId,
                        vector);
                add(srcVecId, tgtVecId, score);
            }
        }
    }

    private void processSparseChangedEvt(VectorSet evtSrc, long vecid, int[] vector) {
        if (evtSrc == this.source) {
            target.rescore(source.key(), vecid, vector, this);
        } else if (evtSrc == this.target) {
            long tgtVecId = vecid;
            TLongIterator iter = sorterKeys.iterator();
            while (iter.hasNext()) {
                long srcVecId = iter.next();
                float score = scoring.score(source.key(), srcVecId, source._get(srcVecId), source.length(srcVecId),
                        target.key(), tgtVecId, vector, vector.length);
                add(srcVecId, tgtVecId, score);
            }
        }
    }

    private void processDeletedEvt(long tgtVecId) {
        if (this.reverseIndexer.containsKey(tgtVecId)) {
            TLongSet range = this.reverseIndexer.get(tgtVecId);
            TLongIterator iter = range.iterator();
            while (iter.hasNext()) {
                long srcVecId = iter.next();
                this.sorters.get(srcVecId).remove(tgtVecId);
            }
        }
        reverseIndexer.remove(tgtVecId);
    }

    public Sorter create(long srcVecId) {
        Sorter sorter = sorters.get(srcVecId);
        if (sorter == null) {
            sorter = new Sorter(this, srcVecId, scoring.order(), this.limit);
            this.sorters.put(srcVecId, sorter);
            this.sorterKeys.add(srcVecId);
        }
        return sorter;
    }

    public void add(long srcVecId, long tgtVecId, float score) {
        create(srcVecId).add(tgtVecId, score);
    }

    public String[] get(long vecid) {
        if (this.sorters.containsKey(vecid)) {
            return this.sorters.get(vecid).pickle();
        } else
            return new String[0];
    }

    public long[] rec(long vecid) {
        if (this.sorters.containsKey(vecid)) {
            return this.sorters.get(vecid).vecids();
        } else
            return new long[0];
    }

    public void remove(long srcVecId, long tgtVecId) {
        this.sorters.get(srcVecId).remove(tgtVecId);
        if (reverseIndexer.containsKey(tgtVecId)) {
            reverseIndexer.get(tgtVecId).remove(srcVecId);
        }
    }

    public void addListener(RecommendationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onVectorAdded(VectorSet evtSrc, long vecid, float[] inputed) {
        processDenseChangedEvt(evtSrc, vecid, inputed);
    }

    @Override
    public void onVectorAdded(VectorSet evtSrc, long vecid, int[] vector) {
        processSparseChangedEvt(evtSrc, vecid, vector);
    }

    @Override
    public void onVectorSetted(VectorSet evtSrc, long vecid, float[] old, float[] vector) {
        if (evtSrc == this.source && sorters.containsKey(vecid)) {
            sorters.get(vecid).reset();
        }
        processDenseChangedEvt(evtSrc, vecid, vector);
    }

    @Override
    public void onVectorSetted(VectorSet evtSrc, long vecid, int[] old, int[] vector) {
        if (evtSrc == this.source && sorters.containsKey(vecid)) {
            sorters.get(vecid).reset();
        }
        processSparseChangedEvt(evtSrc, vecid, vector);
    }

    @Override
    public void onVectorAccumulated(VectorSet evtSrc, long vecid, float[] vector, float[] accumulated) {
        if (evtSrc == this.source && sorters.containsKey(vecid)) {
            sorters.get(vecid).reset();
        }
        processDenseChangedEvt(evtSrc, vecid, accumulated);
    }

    @Override
    public void onVectorAccumulated(VectorSet evtSrc, long vecid, int[] vector, int[] accumulated) {
        if (evtSrc == this.source && sorters.containsKey(vecid)) {
            sorters.get(vecid).reset();
        }
        processSparseChangedEvt(evtSrc, vecid, accumulated);
    }

    @Override
    public void onVectorRemoved(VectorSet evtSrc, long vecid) {
        if (evtSrc == this.target) {
            processDeletedEvt(vecid);
        }
        if (evtSrc == this.source) {
            for (long tgtId : this.sorters.get(vecid).vecids()) {
                if (this.reverseIndexer.containsKey(tgtId)) {
                    this.reverseIndexer.get(tgtId).remove(vecid);
                }
            }
            this.sorters.remove(vecid);
            this.sorterKeys.remove(vecid);
        }
    }
}
