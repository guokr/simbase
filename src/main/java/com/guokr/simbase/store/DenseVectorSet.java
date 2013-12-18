package com.guokr.simbase.store;

import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.List;

import com.guokr.simbase.events.VectorSetListener;

public class DenseVectorSet implements VectorSet {

    private TFloatList              probs   = new TFloatArrayList();
    private TIntIntMap              indexer = new TIntIntHashMap();

    private float                   accumuFactor;
    private int                     sparseFactor;

    private Basis                   base;

    private boolean                 listening;
    private List<VectorSetListener> listeners;

    public DenseVectorSet(Basis base) {
        this(base, 0.01f, 4096);
    }

    public DenseVectorSet(Basis base, float accumuFactor, int sparseFactor) {
        this.base = base;
        this.accumuFactor = accumuFactor;
        this.sparseFactor = sparseFactor;
        this.listening = true;
        this.listeners = new ArrayList<VectorSetListener>();
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

        if (listening) {
            for (VectorSetListener l : listeners) {
                l.onVectorRemoved(this, vecid);
            }
        }
    }

    @Override
    public float[] get(int vecid) {
        float[] result = new float[this.base.size()];
        if (indexer.containsKey(vecid)) {
            float ftmp = 0;
            int cursor = 0;
            int start = indexer.get(vecid);
            while (cursor < result.length) {
                ftmp = probs.get(start + cursor);
                if (ftmp >= 0 && ftmp <= 1) {
                    result[cursor] = ftmp;
                }
                cursor++;
            }
        }
        return result;
    }

    @Override
    public void add(int vecid, float[] vector) {
        if (!indexer.containsKey(vecid)) {
            float length = 0;
            int start = probs.size();
            indexer.put(vecid, start);
            for (float val : vector) {
                probs.add(val);
                length += val * val;
            }
            probs.add((float) (vecid + 1));
            probs.add(length);

            if (listening) {
                for (VectorSetListener l : listeners) {
                    l.onVectorAdded(this, vecid, vector);
                }
            }
        }
    }

    @Override
    public void set(int vecid, float[] vector) {
        if (indexer.containsKey(vecid)) {
            float[] old = get(vecid);

            float length = 0;
            int cursor = indexer.get(vecid);
            for (float val : vector) {
                probs.set(cursor, val);
                length += val * val;
                cursor++;
            }
            probs.set(cursor++, (float) (vecid + 1));
            probs.set(cursor, length);

            if (listening) {
                for (VectorSetListener l : listeners) {
                    l.onVectorSetted(this, vecid, old, vector);
                }
            }
        } else {
            add(vecid, vector);
        }
    }

    @Override
    public void accumulate(int vecid, float[] vector) {
        if (!indexer.containsKey(vecid)) {
            add(vecid, vector);
        } else {
            float length = 0;
            int cursor = indexer.get(vecid);
            for (float newval : vector) {
                float oldval = probs.get(cursor);
                float val = (1f - accumuFactor) * oldval + accumuFactor * newval;
                probs.set(cursor, val);
                length += val * val;
                cursor++;
            }
            probs.set(cursor++, (float) (vecid + 1));
            probs.set(cursor, length);

            if (listening) {
                float[] accumulated = get(vecid);
                for (VectorSetListener l : listeners) {
                    l.onVectorAccumulated(this, vecid, vector, accumulated);
                }
            }
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

    @Override
    public void addListener(VectorSetListener listener) {
        listeners.add(listener);
    }

    @Override
    public void rescore(int srcVecId, float length, float[] vector, Recommendation rec) {
        rec.create(srcVecId);
        float scoring = 0;
        int idx = 0, end = probs.size(), len = vector.length;
        for (int offset = 0; offset < end; offset++) {
            float val = probs.get(offset);
            if (val >= 0) {
                if (val < 1) {
                    if (idx < len) {
                        float another = vector[idx++];// ArrayIndexOutOfBoundsException
                        scoring += another * val;
                    }
                } else {
                    float cosine = scoring / length / probs.get(++offset);
                    int tgtVecId = (int) val - 1;
                    rec.add(srcVecId, tgtVecId, cosine);
                    idx = 0;
                    scoring = 0;
                }
            }
        }
    }

}
