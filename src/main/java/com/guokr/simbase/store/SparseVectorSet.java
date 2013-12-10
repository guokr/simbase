package com.guokr.simbase.store;

import com.guokr.simbase.events.VectorSetListener;

import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TIntIntHashMap;

public class SparseVectorSet implements VectorSet {

    private TFloatList probs   = new TFloatArrayList();
    private TIntIntMap indexer = new TIntIntHashMap();

    private float      accumuFactor;
    private int        sparseFactor;

    private Basis      base;

    public SparseVectorSet(Basis base) {
        this(base, 0.01f, 4096);
    }

    public SparseVectorSet(Basis base, float accumuFactor, int sparseFactor) {
        this.base = base;
        this.accumuFactor = accumuFactor;
        this.sparseFactor = sparseFactor;
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
    }

    @Override
    public float[] get(int vecid) {
        return base.densify(sparseFactor, _get(vecid));
    }

    @Override
    public void add(int vecid, float[] distr) {
        _add(vecid, base.sparsify(sparseFactor, distr));
    }

    @Override
    public void set(int vecid, float[] distr) {
        _set(vecid, base.sparsify(sparseFactor, distr));
    }

    @Override
    public void accumulate(int vecid, float[] distr) {
        _accumulate(vecid, base.sparsify(sparseFactor, distr));
    }

    @Override
    public int[] _get(int vecid) {
        TIntArrayList resultList = new TIntArrayList();
        if (indexer.containsKey(vecid)) {
            int cursor = indexer.get(vecid);
            while (true) {
                int pos = (int) probs.get(cursor++);
                int val = (int) probs.get(cursor++);
                if (pos >= 0 && val >= 0) {
                    resultList.add(pos);
                    resultList.add(val);
                } else {
                    break;
                }
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
        }
    }

    @Override
    public void _set(int vecid, int[] pairs) {
        validateParams(vecid, pairs);
        remove(vecid);
        _add(vecid, pairs);
    }

    @Override
    public void _accumulate(int vecid, int[] pairs) {
        validateParams(vecid, pairs);

        TIntFloatMap results = new TIntFloatHashMap();
        if (indexer.containsKey(vecid)) {
            int cursor = indexer.get(vecid);
            while (true) {
                int pos = (int) probs.get(cursor++);
                float val = probs.get(cursor++) * (1f - accumuFactor);
                if (pos >= 0 && val >= 0) {
                    results.put(pos, val);
                } else {
                    break;
                }
            }
        }

        int cursor = 0;
        while (cursor < pairs.length) {
            int pos = pairs[cursor++];
            float val = (float) pairs[cursor++] * accumuFactor;
            if (results.containsKey(pos)) {
                results.put(pos, results.get(pos) + val);
            } else {
                results.put(pos, val);
            }
        }

        remove(vecid);

        int start = probs.size();
        indexer.put(vecid, start);
        TIntFloatIterator iter = results.iterator();
        while (iter.hasNext()) {
            probs.add(iter.key());
            probs.add(iter.value());
        }
        probs.add(-(vecid + 1));
    }

    @Override
    public void addListener(VectorSetListener listener) {
    }
}
