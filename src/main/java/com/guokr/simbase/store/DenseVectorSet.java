package com.guokr.simbase.store;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.guokr.simbase.events.BasisListener;
import com.guokr.simbase.events.VectorSetListener;

public class DenseVectorSet implements VectorSet, BasisListener {

    public static final String      TYPE      = "dense";

    String                          key;

    TFloatList                      data      = new TFloatArrayList();
    TIntIntMap                      lengths   = new TIntIntHashMap();
    TIntIntMap                      indexer   = new TIntIntHashMap();

    float                           accumuFactor;
    int                             sparseFactor;

    Basis                           base;

    private boolean                 listening = true;
    private List<VectorSetListener> listeners = new ArrayList<VectorSetListener>();

    private int[]                   iReuseList;
    private float[]                 fReuseList;

    public DenseVectorSet(String key, Basis base) {
        this(key, base, 0.01f, 4096);
    }

    public DenseVectorSet(String key, Basis base, float accumuFactor,
            int sparseFactor) {
        this.key = key;
        this.base = base;
        this.accumuFactor = accumuFactor;
        this.sparseFactor = sparseFactor;

        this.fReuseList = new float[this.base.size()];
        this.iReuseList = new int[this.base.size() * 2];
        this.base.addListener(this);
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
    public int size() {
        return this.indexer.size();
    }

    @Override
    public boolean contains(int vecid) {
        return this.indexer.containsKey(vecid);
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

    protected void get(int vecid, float[] result) {
        int len = lengths.get(vecid);
        int start = indexer.get(vecid);
        data.toArray(result, start, len);
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
            int start = data.size();
            indexer.put(vecid, start);
            for (float val : vector) {
                data.add(val);
            }
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
                    data.set(cursor, val);
                    cursor++;
                }
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
            float max = Float.NEGATIVE_INFINITY;
            if (lengths.get(vecid) != vector.length) {
                float[] oldvec = get(vecid);
                remove(vecid);
                indexer.put(vecid, data.size());
                lengths.put(vecid, vector.length);

                int cursor = 0;
                for (float newval : vector) {
                    float oldval = oldvec[cursor];
                    float val = oldval + newval;
                    data.add(val);
                    if (max < val) {
                        max = val;
                    }
                    cursor++;
                }
            } else {
                int cursor = indexer.get(vecid);
                for (float newval : vector) {
                    float oldval = data.get(cursor);
                    float val = oldval + newval;
                    data.set(cursor, val);
                    if (max < val) {
                        max = val;
                    }
                    cursor++;
                }
            }

            if (max > accumuFactor) {
                int cursor = indexer.get(vecid);
                int len = lengths.get(vecid);
                for (int i = 0; i < len; i++) {
                    data.set(cursor + i, data.get(cursor + i) * accumuFactor
                            / max);
                }
            }

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
    public void rescore(String key, int vecid, float[] vector,
            Recommendation rec) {
        rec.create(vecid);
        TIntIntIterator iter = indexer.iterator();
        if (this == rec.source) {
            while (iter.hasNext()) {
                iter.advance();
                int tgtId = iter.key();
                get(tgtId, fReuseList);
                float score = rec.scoring.score(key, vecid, vector, this.key,
                        tgtId, fReuseList);
                rec.add(vecid, tgtId, score);
                rec.add(tgtId, vecid, score);
            }
            rec.remove(vecid, vecid);
        } else {
            while (iter.hasNext()) {
                iter.advance();
                int tgtId = iter.key();
                get(tgtId, fReuseList);
                float score = rec.scoring.score(key, vecid, vector, this.key,
                        tgtId, fReuseList);
                rec.add(vecid, tgtId, score);
            }
        }
    }

    @Override
    public void rescore(String key, int vecid, int[] vector, Recommendation rec) {
        rec.create(vecid);
        TIntIntIterator iter = indexer.iterator();
        float[] input = new float[this.base.size()];
        if (this == rec.source) {
            while (iter.hasNext()) {
                iter.advance();
                int tgtId = iter.key();
                _get(tgtId, input, iReuseList);
                float score = rec.scoring.score(key, vecid, vector,
                        vector.length, this.key, tgtId, iReuseList,
                        length(tgtId));
                rec.add(vecid, tgtId, score);
                rec.add(tgtId, vecid, score);
            }
            rec.remove(vecid, vecid);
        } else {
            while (iter.hasNext()) {
                iter.advance();
                int tgtId = iter.key();
                _get(tgtId, input, iReuseList);
                float score = rec.scoring.score(key, vecid, vector,
                        vector.length, this.key, tgtId, iReuseList,
                        length(tgtId));
                rec.add(vecid, tgtId, score);
            }
        }
    }

    @Override
    public void onBasisRevised(Basis evtSrc, String[] oldSchema,
            String[] newSchema) {
        this.fReuseList = new float[this.base.size()];
        this.iReuseList = new int[this.base.size() * 2];
    }

}
