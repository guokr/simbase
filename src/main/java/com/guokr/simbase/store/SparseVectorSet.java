package com.guokr.simbase.store;

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

    private int length(int[] pairs) {
        int result = 0;
        int len = pairs.length;
        for (int i = 0; i < len;) {
            result += pairs[i + 1] * pairs[i + 1];
            i += 2;
        }
        return result;
    }

    @Override
    public int[] ids() {
        TIntArrayList resultList = new TIntArrayList();
        int end = probs.size();
        for (int offset = 0; offset < end;) {
            float pos = probs.get(offset);
            if (pos < -1) {
                resultList.add(-(int) pos - 1);
            }
            offset += 2;
        }
        int[] result = new int[resultList.size()];
        return resultList.toArray(result);
    }

    @Override
    public void remove(int vecid) {
        if (indexer.containsKey(vecid)) {
            int cursor = indexer.get(vecid);
            while (true) {
                float val = probs.get(cursor);
                if (val < 0) {
                    probs.set(cursor, -1);
                    probs.set(cursor + 1, -1);
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
        TIntArrayList resultList = new TIntArrayList(base.size());
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
        return resultList.toArray(result);
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
            probs.add(length(pairs));

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
                if (pos < 0) {
                    break;
                }
                float val = probs.get(cursor++) * (1f - accumuFactor);
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

            listening = false;
            remove(vecid);
            listening = true;

            int start = probs.size();
            indexer.put(vecid, start);
            TIntIterator iter = indexes.iterator();
            float length = 0f;
            while (iter.hasNext()) {
                int key = iter.next();
                float value = results.get(key);
                probs.add(key);
                probs.add(value);
                length += value * value;
            }
            probs.add(-(vecid + 1));
            probs.add(length);

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
    public void rescore(int srcVecId, int length, int[] vector, Recommendation rec) {
        rec.create(srcVecId);
        float scoring = 0;
        int end = probs.size(), srcLen = vector.length;
        for (int base = 0; base < end;) {
            int tgtIdx = (int) probs.get(base);
            int srcOffset = 0, tgtOffset = base;
            while (tgtIdx >= 0 && srcOffset < srcLen) {
                int srcIdx = vector[srcOffset];
                if (srcIdx == tgtIdx) {
                    scoring += vector[srcOffset + 1] * probs.get(tgtOffset + 1);
                    tgtOffset += 2;
                    tgtIdx = (int) probs.get(tgtOffset);
                    srcOffset += 2;
                } else if (srcIdx > tgtIdx) {
                    tgtOffset += 2;
                    tgtIdx = (int) probs.get(tgtOffset);
                } else {
                    srcOffset += 2;
                }
            }
            int tgtVecId = -(int) (probs.get(tgtOffset) + 1);
            float tgtLength = probs.get(tgtOffset + 1);
            float cosine = scoring * scoring / length / tgtLength;
            base = tgtOffset + 2;
            if (!(this == rec.source && srcVecId == tgtVecId)) {
                rec.add(srcVecId, tgtVecId, cosine);
            }
            scoring = 0;
        }
    }

}
