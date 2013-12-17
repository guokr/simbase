package com.guokr.simbase.store;

import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.list.TFloatList;
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

    private TFloatList              probs   = new TFloatArrayList();
    private TIntIntMap              indexer = new TIntIntHashMap();

    private float                   accumuFactor;
    private int                     sparseFactor;

    private Basis                   base;

    private boolean                 listening;
    private List<VectorSetListener> listeners;

    public SparseVectorSet(Basis base) {
        this(base, 0.01f, 4096);
    }

    public SparseVectorSet(Basis base, float accumuFactor, int sparseFactor) {
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
    public void remove(int vecid) {
        if (indexer.containsKey(vecid)) {
            int cursor = indexer.get(vecid);
            while (true) {
                float val = probs.get(cursor);
                if (val < 0) {
                    break;
                }
                probs.set(cursor, -1);
                cursor++;
            }
        }

        indexer.remove(vecid);

        if (listening) {
            for (VectorSetListener l : listeners) {
                l.onVectorRemoved(this, vecid);
            }
        }
    }

    @Override
    public float[] get(int vecid) {
        return base.densify(sparseFactor, _get(vecid));
    }

    @Override
    public void add(int vecid, float[] vector) {
        _add(vecid, base.sparsify(sparseFactor, vector));
    }

    @Override
    public void set(int vecid, float[] vector) {
        _set(vecid, base.sparsify(sparseFactor, vector));
    }

    @Override
    public void accumulate(int vecid, float[] vector) {
        _accumulate(vecid, base.sparsify(sparseFactor, vector));
    }

    @Override
    public int[] _get(int vecid) {
        TIntArrayList resultList = new TIntArrayList();
        if (indexer.containsKey(vecid)) {
            int cursor = indexer.get(vecid);
            while (true) {
                int pos = (int) probs.get(cursor++);
                if (pos < 0) {
                    break;
                }
                int val = Math.round(probs.get(cursor++));
                resultList.add(pos);
                resultList.add(val);
            }
        }
        int[] result = new int[resultList.size()];
        resultList.toArray(result);
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
                float[] input = base.densify(sparseFactor, pairs);
                for (VectorSetListener l : listeners) {
                    l.onVectorAdded(this, vecid, input);
                }
            }
        }
    }

    @Override
    public void _set(int vecid, int[] pairs) {
        validateParams(vecid, pairs);
        if (indexer.containsKey(vecid)) {
            float[] old = base.densify(sparseFactor, _get(vecid));

            listening = false;
            remove(vecid);
            _add(vecid, pairs);
            listening = true;

            if (listening) {
                float[] input = base.densify(sparseFactor, pairs);
                for (VectorSetListener l : listeners) {
                    l.onVectorSetted(this, vecid, old, input);
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
            TIntFloatMap results = new TIntFloatHashMap();

            int cursor = indexer.get(vecid);
            while (true) {
                int pos = (int) probs.get(cursor++);
                if (pos < 0) {
                    break;
                }
                float val = probs.get(cursor++) * (1f - accumuFactor);
                results.put(pos, val);

            }

            cursor = 0;
            while (cursor < pairs.length) {
                int pos = pairs[cursor++];
                float val = (float) pairs[cursor++] * accumuFactor;
                if (results.containsKey(pos)) {
                    results.put(pos, results.get(pos) + val);
                } else {
                    results.put(pos, val);
                }
            }

            listening = false;
            remove(vecid);
            listening = true;

            int start = probs.size();
            indexer.put(vecid, start);
            TIntFloatIterator iter = results.iterator();
            while (iter.hasNext()) {
                iter.advance();
                probs.add(iter.key());
                probs.add(iter.value());
            }
            probs.add(-(vecid + 1));

            if (listening) {
                float[] input = base.densify(sparseFactor, pairs);
                float[] accumulated = base.densify(sparseFactor, _get(vecid));
                for (VectorSetListener l : listeners) {
                    l.onVectorAccumulated(this, vecid, input, accumulated);
                }
            }
        }
    }

    @Override
    public void addListener(VectorSetListener listener) {
        listeners.add(listener);
    }

    @Override
    public void score(int vecid, float length, float[] vector, Recommendation rec) {
    }

}
