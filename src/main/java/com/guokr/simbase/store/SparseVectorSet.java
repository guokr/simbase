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

    TFloatList                      probs   = new TFloatArrayList();
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
        if (vecid < 0) {
            throw new IllegalArgumentException("the vector id must be a positive number!");
        }
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("the size of the input array must be a even number!");
        }
        for (int val : pairs) {
            if (val < 0) {
                throw new IllegalArgumentException("the elements in the input array must be greater than zero!");
            }
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
        TFloatList tmp = probs;
        probs = new TFloatArrayList();
        indexer = new TIntIntHashMap();
        int end = tmp.size();
        int curbegin = -1;
        for (int offset = 0; offset < end; offset++) {
            float val = tmp.get(offset);
            if (curbegin == -1) {
                curbegin = offset;
            }
            if (val != -1) {
                if (val < -1) {
                    indexer.put(-(int) val - 1, curbegin);
                    curbegin = -1;
                }
                probs.add(val);
            }

        }
    }

    @Override
    public int[] ids() {
        int[] result = new int[indexer.size()];
        int end = probs.size();
        int pos = 0;
        for (int offset = 0; offset < end;) {
            float value = probs.get(offset);
            if (value < -1) {
                result[pos] = -(int) value - 1;
                pos++;
            }
            offset += 1;
        }
        return result;
    }

    @Override
    public void remove(int vecid) {
        if (indexer.containsKey(vecid)) {
            int cursor = indexer.get(vecid);
            while (true) {
                float val = probs.get(cursor);
                if (val < 0) {
                    probs.set(cursor, -1);
                    break;
                }
                probs.set(cursor, -1);
                cursor++;
            }

            indexer.remove(vecid);

            if (listening) {
                for (VectorSetListener l : listeners) {
                    l.onVectorRemoved(this, vecid);
                }
            }
        }
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
        int cursor = indexer.get(vecid), i = 0;
        while (true) {
            int pos = (int) probs.get(cursor++);
            if (pos < 0) {
                break;
            }
            int val = Math.round(probs.get(cursor++));
            result[i++] = pos;
            result[i++] = val;
        }
    }

    @Override
    public int[] _get(int vecid) {
        int[] result = new int[base.size() * 2];
        _get(vecid, result);
        return result;
    }

    @Override
    public void _add(int vecid, int[] pairs) {
        validateParams(vecid, pairs);
        if (!indexer.containsKey(vecid)) {
            int start = probs.size();
            indexer.put(vecid, start);
            for (int val : pairs) {
                probs.add(val);
            }
            probs.add(-(vecid + 1));

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
            while (true) {
                int pos = (int) probs.get(cursor++);
                probs.set(cursor - 1, -1);
                if (pos < 0) {
                    break;
                }
                float val = probs.get(cursor++) * (1f - accumuFactor);
                probs.set(cursor - 1, -1);
                results.put(pos, val);
                indexes.add(pos);
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

            int start = probs.size();
            indexer.put(vecid, start);
            TIntIterator iter = indexes.iterator();
            while (iter.hasNext()) {
                int key = iter.next();
                float value = results.get(key);
                probs.add(key);
                probs.add(value);
            }
            probs.add(-(vecid + 1));

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
                float score = rec.scoring.score(key, vecid, vector, this.key, tgtId, target);
                rec.add(vecid, tgtId, score);
                rec.add(tgtId, vecid, score);
            }
            rec.remove(vecid, vecid);
        } else {
            while (iter.hasNext()) {
                iter.advance();
                int tgtId = iter.key();
                _get(tgtId, target);
                float score = rec.scoring.score(key, vecid, vector, this.key, tgtId, target);
                rec.add(vecid, tgtId, score);
            }
        }
    }
}
