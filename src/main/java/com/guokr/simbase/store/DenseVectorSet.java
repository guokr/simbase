package com.guokr.simbase.store;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.guokr.simbase.events.VectorSetListener;

public class DenseVectorSet implements VectorSet {

    public static final String      TYPE      = "dense";

    String                          key;

    TFloatList                      probs     = new TFloatArrayList();
    TIntIntMap                      lengths   = new TIntIntHashMap();
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
        lengths = new TIntIntHashMap();
        indexer = new TIntIntHashMap();
        int end = tmp.size();
        int curbegin = -1, curlen = 0;
        int vecid;
        for (int offset = 0; offset < end; offset++) {
            float val = tmp.get(offset);
            if (val >= 0) {
                if (curbegin == -1) {
                    curbegin = offset;
                }
                if (val > 1) {
                    vecid = (int) val - 1;
                    indexer.put(vecid, curbegin);
                    lengths.put(vecid, curlen);
                    curbegin = -1;
                    curlen = 0;
                }
                probs.add(val);
                curlen++;
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
            lengths.remove(vecid);

            if (listening) {
                for (VectorSetListener l : listeners) {
                    l.onVectorRemoved(this, vecid);
                }
            }
        }
    }

    protected void get(int vecid, float[] result) {
        int len = lengths.get(vecid);
        int start = indexer.get(vecid);
        probs.toArray(result, start, len);
        Arrays.fill(result, len, result.length, 0);
    }

    @Override
    public float[] get(int vecid) {
        float[] result;
        if (indexer.containsKey(vecid)) {
            result = new float[this.base.size()];
            get(vecid, result);
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
            lengths.put(vecid, vector.length);

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

            if (lengths.get(vecid) != vector.length) {
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

    protected void _get(int vecid, float[] input, int[] result) {
        get(vecid, input);
        Basis.sparsify(sparseFactor, input, result);
    }

    @Override
    public int[] _get(int vecid) {
        int[] result = new int[this.base.size()];
        float[] input = new float[this.base.size()];
        _get(vecid, input, result);
        return result;
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
        float[] target = new float[this.base.size()];
        if (this == rec.source) {
            while (iter.hasNext()) {
                iter.advance();
                int tgtId = iter.key();
                get(tgtId, target);
                float score = rec.scoring.score(key, vecid, vector, this.key, tgtId, target);
                rec.add(vecid, tgtId, score);
                rec.add(tgtId, vecid, score);
            }
            rec.remove(vecid, vecid);
        } else {
            while (iter.hasNext()) {
                iter.advance();
                int tgtId = iter.key();
                get(tgtId, target);
                float score = rec.scoring.score(key, vecid, vector, this.key, tgtId, target);
                rec.add(vecid, tgtId, score);
            }
        }
    }

    @Override
    public void rescore(String key, int vecid, int[] vector, Recommendation rec) {
        rec.create(vecid);
        TIntIntIterator iter = indexer.iterator();
        float[] input = new float[this.base.size()];
        int[] target = new int[this.base.size() * 2];
        if (this == rec.source) {
            while (iter.hasNext()) {
                iter.advance();
                int tgtId = iter.key();
                _get(tgtId, input, target);
                float score = rec.scoring.score(key, vecid, vector, this.key, tgtId, target);
                rec.add(vecid, tgtId, score);
                rec.add(tgtId, vecid, score);
            }
            rec.remove(vecid, vecid);
        } else {
            while (iter.hasNext()) {
                iter.advance();
                int tgtId = iter.key();
                _get(tgtId, input, target);
                float score = rec.scoring.score(key, vecid, vector, this.key, tgtId, target);
                rec.add(vecid, tgtId, score);
            }
        }
    }
}
