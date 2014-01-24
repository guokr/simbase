package com.guokr.simbase.store;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.List;

import com.guokr.simbase.events.VectorSetListener;

public class SparseVectorSet implements VectorSet {

    public static final String      TYPE    = "sparse";

    String                          key;

    TFloatList                      data   = new TFloatArrayList();
    TIntIntMap                      lengths = new TIntIntHashMap();
    TIntIntMap                      indexer = new TIntIntHashMap();

    float                           accumuFactor;
    int                             sparseFactor;

    Basis                           base;

    private boolean                 listening;
    private List<VectorSetListener> listeners;

    public SparseVectorSet(String key, Basis base) {
        this(key, base, 0.01f, 4096);
    }

    public SparseVectorSet(String key, Basis base, float accumuFactor, int sparseFactor) {
        this.key = key;
        this.base = base;
        this.accumuFactor = accumuFactor;
        this.sparseFactor = sparseFactor;
        this.listening = true;
        this.listeners = new ArrayList<VectorSetListener>();
    }

    private void validateParams(int vecid, int[] pairs) {
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("the size of the input array must be a even number!");
        }
    }

    @Override
    public String type() {
        return "" + TYPE;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public void clean() {
        TFloatList olddata = data;
        TIntIntMap oldindexer = indexer;
        data = new TFloatArrayList(olddata.size());
        indexer = new TIntIntHashMap(oldindexer.size());

        int pos = 0;
        TIntIntIterator iter = oldindexer.iterator();
        while (iter.hasNext()) {
            iter.advance();
            int vecid = iter.key();
            int start = iter.value();
            int length = lengths.get(vecid);

            int cursor = 0;
            indexer.put(vecid, pos);
            while (cursor < length) {
                data.add(olddata.get(start + cursor));
                pos++;
                cursor++;
            }
        }
    }

    @Override
    public int[] ids() {
        return indexer.keys();
    }

    @Override
    public void remove(int vecid) {
        if (indexer.containsKey(vecid)) {
            indexer.remove(vecid);
            lengths.remove(vecid);

            if (listening) {
                for (VectorSetListener l : listeners) {
                    l.onVectorRemoved(this, vecid);
                }
            }
        }
    }

    @Override
    public int length(int vecid) {
        return lengths.get(vecid);
    }

    public float[] get(int vecid, int[] input, float[] result) {
        _get(vecid, input);
        Basis.densify(base.size(), sparseFactor, input, result);
        return result;
    }

    @Override
    public float[] get(int vecid) {
        float[] result = new float[base.size()];
        Basis.densify(base.size(), sparseFactor, _get(vecid), result);
        return result;
    }

    @Override
    public void add(int vecid, float[] vector) {
        _add(vecid, Basis.sparsify(sparseFactor, vector));
    }

    @Override
    public void set(int vecid, float[] vector) {
        _set(vecid, Basis.sparsify(sparseFactor, vector));
    }

    @Override
    public void accumulate(int vecid, float[] vector) {
        _accumulate(vecid, Basis.sparsify(sparseFactor, vector));
    }

    protected void _get(int vecid, int[] result) {
        int length = lengths.get(vecid);
        int cursor = indexer.get(vecid), i = 0;
        while (length > 0) {
            int pos = (int) data.get(cursor++);
            int val = Math.round(data.get(cursor++));
            result[i++] = pos;
            result[i++] = val;
            length -= 2;
        }
    }

    @Override
    public int[] _get(int vecid) {
        int[] result = new int[lengths.get(vecid)];
        _get(vecid, result);
        return result;
    }

    @Override
    public void _add(int vecid, int[] pairs) {
        validateParams(vecid, pairs);
        if (!indexer.containsKey(vecid)) {
            int start = data.size();
            indexer.put(vecid, start);
            lengths.put(vecid, pairs.length);
            for (int val : pairs) {
                data.add(val);
            }

            if (listening) {
                for (VectorSetListener l : listeners) {
                    l.onVectorAdded(this, vecid, pairs);
                }
            }
        }
    }

    @Override
    public void _set(int vecid, int[] pairs) {
        validateParams(vecid, pairs);
        if (indexer.containsKey(vecid)) {
            int[] old = _get(vecid);

            listening = false;
            remove(vecid);
            _add(vecid, pairs);
            listening = true;

            if (listening) {
                for (VectorSetListener l : listeners) {
                    l.onVectorSetted(this, vecid, old, pairs);
                }
            }
        } else {
            _add(vecid, pairs);
        }
    }

    @Override
    public void _accumulate(int vecid, int[] pairs) {
        validateParams(vecid, pairs);
        if (!indexer.containsKey(vecid)) {
            _add(vecid, pairs);
        } else {
            TIntList indexes = new TIntArrayList();
            TIntFloatMap results = new TIntFloatHashMap();

            int cursor = indexer.get(vecid);
            int length = lengths.get(vecid);
            while (length > 0) {
                int pos = (int) data.get(cursor++);
                float val = data.get(cursor++) * (1f - accumuFactor);
                results.put(pos, val);
                indexes.add(pos);
                length -= 2;
            }

            cursor = 0;
            while (cursor < pairs.length) {
                int pos = pairs[cursor++];
                float val = (float) pairs[cursor++] * accumuFactor;
                if (results.containsKey(pos)) {
                    results.put(pos, results.get(pos) + val);
                } else {
                    results.put(pos, val);
                    indexes.add(pos);
                }
            }
            indexes.sort();

            int start = data.size();
            indexer.put(vecid, start);
            lengths.put(vecid,  indexes.size() * 2);
            TIntIterator iter = indexes.iterator();
            while (iter.hasNext()) {
                int key = iter.next();
                float value = results.get(key);
                data.add(key);
                data.add(value);
            }

            if (listening) {
                int[] accumulated = _get(vecid);
                for (VectorSetListener l : listeners) {
                    l.onVectorAccumulated(this, vecid, pairs, accumulated);
                }
            }
        }
    }

    @Override
    public void addListener(VectorSetListener listener) {
        listeners.add(listener);
    }

    @Override
    public void rescore(String key, int vecid, float[] vector, Recommendation rec) {
        rec.create(vecid);
        TIntIntIterator iter = indexer.iterator();
        int[] input = new int[this.base.size() * 2];
        float[] target = new float[this.base.size()];
        if (this == rec.source) {
            while (iter.hasNext()) {
                iter.advance();
                int tgtId = iter.key();
                get(tgtId, input, target);
                float score = rec.scoring.score(key, vecid, vector, this.key, tgtId, target);
                rec.add(vecid, tgtId, score);
                rec.add(tgtId, vecid, score);
            }
            rec.remove(vecid, vecid);
        } else {
            while (iter.hasNext()) {
                iter.advance();
                int tgtId = iter.key();
                get(tgtId, input, target);
                float score = rec.scoring.score(key, vecid, vector, this.key, tgtId, target);
                rec.add(vecid, tgtId, score);
            }
        }
    }

    @Override
    public void rescore(String key, int vecid, int[] vector, Recommendation rec) {
        rec.create(vecid);
        TIntIntIterator iter = indexer.iterator();
        int[] target = new int[this.base.size() * 2];
        if (this == rec.source) {
            while (iter.hasNext()) {
                iter.advance();
                int tgtId = iter.key();
                _get(tgtId, target);
                float score = rec.scoring.score(key, vecid, vector, vector.length, this.key, tgtId, target,
                        length(tgtId));
                rec.add(vecid, tgtId, score);
                rec.add(tgtId, vecid, score);
            }
            rec.remove(vecid, vecid);
        } else {
            while (iter.hasNext()) {
                iter.advance();
                int tgtId = iter.key();
                _get(tgtId, target);
                float score = rec.scoring.score(key, vecid, vector, vector.length, this.key, tgtId, target,
                        length(tgtId));
                rec.add(vecid, tgtId, score);
            }
        }
    }
}
