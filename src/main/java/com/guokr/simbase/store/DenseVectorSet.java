package com.guokr.simbase.store;

import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

public class DenseVectorSet implements VectorSet {

    private TFloatList probs   = new TFloatArrayList();
    private TIntIntMap indexer = new TIntIntHashMap();

    private float      accumuFactor;
    int                sparseFactor;

    private Basis      base;

    public DenseVectorSet(Basis base) {
        this(base, 0.01f, 4096);
    }

    public DenseVectorSet(Basis base, float accumuFactor, int sparseFactor) {
        this.base = base;
        this.accumuFactor = accumuFactor;
        this.sparseFactor = sparseFactor;
    }

    @Override
    public void remove(int vecid) {
        if (indexer.containsKey(vecid)) {
            int cursor = indexer.get(vecid);
            while (true) {
                float val = probs.get(cursor);
                if (val < 0f) {
                    break;
                }
                if (val >= 1f) {
                    probs.set(cursor, -1);
                    cursor++;
                    val = probs.get(cursor);
                    probs.set(cursor, -1);
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
        float[] result = new float[this.base.size()];
        if (indexer.containsKey(vecid)) {
            float ftmp = 0;
            int cursor = 0;
            int start = indexer.get(vecid);
            while ((ftmp = probs.get(start + cursor++)) >= 0 && (ftmp < 1)) {
                result[cursor] = ftmp;
            }
        }
        return result;
    }

    @Override
    public void add(int vecid, float[] distr) {
        if (!indexer.containsKey(vecid)) {
            float length = 0;
            int start = probs.size();
            indexer.put(vecid, start);
            for (float val : distr) {
                probs.add(val);
                length += val * val;
            }
            probs.add((float) (vecid + 1));
            probs.add(length);
        }
    }

    @Override
    public void set(int vecid, float[] distr) {
        if (indexer.containsKey(vecid)) {
            float length = 0;
            int cursor = indexer.get(vecid);
            for (float val : distr) {
                probs.set(cursor, val);
                length += val * val;
                cursor++;
            }
            probs.set(cursor++, (float) (vecid + 1));
            probs.set(cursor, length);
        }
    }

    @Override
    public void accumulate(int vecid, float[] distr) {
        if (indexer.containsKey(vecid)) {
            float length = 0;
            int cursor = indexer.get(vecid);
            for (float newval : distr) {
                float oldval = probs.get(cursor);
                float val = (1f - accumuFactor) * oldval + accumuFactor * newval;
                probs.set(cursor, val);
                length += val * val;
                cursor++;
            }
            probs.set(cursor++, (float) (vecid + 1));
            probs.set(cursor, length);
        }
    }

    @Override
    public int[] _get(int vecid) {
        return base.sparsify(sparseFactor, get(vecid));
    }

    @Override
    public void _add(int vecid, int[] pairs) {
        this.add(vecid, base.densify(sparseFactor, pairs));
    }

    @Override
    public void _set(int vecid, int[] pairs) {
        this.set(vecid, base.densify(sparseFactor, pairs));
    }

    @Override
    public void _accumulate(int vecid, int[] pairs) {
        this.accumulate(vecid, base.densify(sparseFactor, pairs));
    }

}
