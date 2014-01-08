package com.guokr.simbase.store;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.List;

import com.guokr.simbase.events.VectorSetListener;

public class DenseVectorSet implements VectorSet {

    public static final String      TYPE      = "dense";

    String                          key;

    TFloatList                      probs     = new TFloatArrayList();
    TIntIntMap                      dimns     = new TIntIntHashMap();
    TIntIntMap                      indexer   = new TIntIntHashMap();

    float                           accumuFactor;
    int                             sparseFactor;

    Basis                           base;

    private boolean                 listening = true;
    private List<VectorSetListener> listeners = new ArrayList<VectorSetListener>();

    public DenseVectorSet(String key, Basis base) {
        this(key, base, 0.01f, 4096);
    }

    public DenseVectorSet(String key, Basis base, float accumuFactor, int sparseFactor) {
        this.key = key;
        this.base = base;
        this.accumuFactor = accumuFactor;
        this.sparseFactor = sparseFactor;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public void clean() {
        TFloatList tmp = probs;
        probs = new TFloatArrayList();
        dimns = new TIntIntHashMap();
        indexer = new TIntIntHashMap();
        int end = tmp.size();
        int curbegin = -1, curdim = 0;
        for (int offset = 0; offset < end; offset++) {
            float val = tmp.get(offset);
            if (val >= 0) {
                if (curbegin == -1) {
                    curbegin = offset;
                }

                probs.add(val);
                curdim++;

                if (val > 1) {
                    indexer.put((int) val - 1, curbegin);
                    dimns.put((int) val - 1, curdim);
                    curbegin = -1;
                    curdim = 0;
                }
            }
        }
    }

    @Override
    public int[] ids() {
        int[] result = new int[indexer.size()];
        int end = probs.size();
        int pos = 0;
        for (int offset = 0; offset < end; offset++) {
            float val = probs.get(offset);
            if (val > 1) {
                result[pos] = (int) val - 1;
                offset++;
                pos++;
            }
        }
        return result;
    }

    @Override
    public void remove(int vecid) {
        if (indexer.containsKey(vecid)) {
            int cursor = indexer.get(vecid);
            while (true) {
                float val = probs.get(cursor);
                if (val < 0f) {
                    break;
                } else if (val <= 1) {
                    probs.set(cursor, -1);
                    cursor++;
                } else {
                    probs.set(cursor, -1);
                    break;
                }
            }

            indexer.remove(vecid);
            dimns.remove(vecid);

            if (listening) {
                for (VectorSetListener l : listeners) {
                    l.onVectorRemoved(this, vecid);
                }
            }
        }
    }

    @Override
    public float[] get(int vecid) {
        float[] result;
        if (indexer.containsKey(vecid)) {
            result = new float[this.base.size()];
            float ftmp = 0;
            int cursor = 0;
            int dim = dimns.get(vecid);
            int start = indexer.get(vecid);
            while (cursor < result.length) {
                if (cursor < dim) {
                    ftmp = probs.get(start + cursor);
                    if (ftmp >= 0 && ftmp <= 1) {
                        result[cursor] = ftmp;
                    }
                } else {
                    result[cursor] = 0;
                }
                cursor++;
            }
        } else {
            result = new float[0];
        }
        return result;
    }

    @Override
    public void add(int vecid, float[] vector) {
        if (!indexer.containsKey(vecid)) {
            int start = probs.size();
            indexer.put(vecid, start);
            for (float val : vector) {
                probs.add(val);
            }
            probs.add((float) (vecid + 1));
            dimns.put(vecid, vector.length);

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

            if (dimns.get(vecid) != vector.length) {
                remove(vecid);
                add(vecid, vector);
            } else {
                int cursor = indexer.get(vecid);
                for (float val : vector) {
                    probs.set(cursor, val);
                    cursor++;
                }
                probs.set(cursor++, (float) (vecid + 1));
            }
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
            int cursor = indexer.get(vecid);
            for (float newval : vector) {
                float oldval = probs.get(cursor);
                float val = (1f - accumuFactor) * oldval + accumuFactor * newval;
                probs.set(cursor, val);
                cursor++;
            }
            probs.set(cursor++, (float) (vecid + 1));

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
        return Basis.sparsify(sparseFactor, get(vecid));
    }

    @Override
    public void _add(int vecid, int[] pairs) {
        this.add(vecid, Basis.densify(base.size(), sparseFactor, pairs));
    }

    @Override
    public void _set(int vecid, int[] pairs) {
        this.set(vecid, Basis.densify(base.size(), sparseFactor, pairs));
    }

    @Override
    public void _accumulate(int vecid, int[] pairs) {
        this.accumulate(vecid, Basis.densify(base.size(), sparseFactor, pairs));
    }

    @Override
    public void addListener(VectorSetListener listener) {
        listeners.add(listener);
    }

    @Override
    public void rescore(String key, int vecid, float[] vector, Recommendation rec) {
        rec.create(vecid);
        TIntIntIterator iter = indexer.iterator();
        while (iter.hasNext()) {
            iter.advance();
            int tgtId = iter.key();
            float[] target = get(tgtId);
            float score = rec.scoring.score(key, vecid, vector, this.key, tgtId, target);
            if (!(this == rec.source && vecid == tgtId)) {
                rec.add(vecid, tgtId, score);
                if (this == rec.target) {
                    rec.add(tgtId, vecid, score);
                }
            }
        }
    }

    @Override
    public void rescore(String key, int vecid, int[] vector, Recommendation rec) {
        rec.create(vecid);
        TIntIntIterator iter = indexer.iterator();
        while (iter.hasNext()) {
            iter.advance();
            int tgtId = iter.key();
            int[] target = _get(tgtId);
            float score = rec.scoring.score(key, vecid, vector, this.key, tgtId, target);
            if (!(this == rec.source && vecid == tgtId)) {
                rec.add(vecid, tgtId, score);
                if (this == rec.target) {
                    rec.add(tgtId, vecid, score);
                }
            }
        }
    }

}
