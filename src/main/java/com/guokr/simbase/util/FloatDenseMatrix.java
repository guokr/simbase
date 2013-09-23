package com.guokr.simbase.util;

import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.Map;

public class FloatDenseMatrix extends AbstractVectorSet {
    private TIntIntMap vecidToidx = new TIntIntHashMap();
    private TFloatList hive       = new TFloatArrayList();

    public FloatDenseMatrix(Map<String, Object> config, Basis basis) {
        super(config, basis);
    }

    @Override
    public void set(int vecid, float[] distr) throws Exception {
        if (distr.length > getBasis().size()) {
            throw new Exception("Invalid vector length");
        }
        int idx = 0;
        int size = 0;
        if (vecidToidx.containsKey(vecid)) {
            idx = vecidToidx.get(vecid);
            size = getSlotSize(idx);
            if (size < distr.length) {
                discardSlot(idx);
                allocSlot(size, vecid, distr);
            } else {
                updateSlot(idx, distr);
            }
        } else {
            size = distr.length;
            allocSlot(size, vecid, distr);
        }
    }

    private int getSlotSize(int idx) {
        int size = 0;
        while (hive.get(idx++) < 1) {
            size++;
        }
        return size;
    }

    private void updateSlot(int idx, float[] distr) {
        float length = 0;
        for (float val : distr) {
            assert hive.get(idx) < 1;
            hive.set(idx++, val);
            length += val * val;
        }
        assert hive.get(idx) > 1;
        hive.set(++idx, length);
    }

    private void discardSlot(int begin) {
        while (hive.get(begin) < 1) {
            hive.set(begin++, -1);
        }
        hive.set(begin++, -1);
        hive.set(begin, -1);
    }

    private void allocSlot(int size, int vecid, float[] distr) {
        float length = 0;
        int idx = hive.size();
        for (float val : distr) {
            hive.add(val);
            length += val * val;
        }
        hive.add((float) (vecid + 1));
        hive.add(length);

        vecidToidx.put(vecid, idx);
    }

    @Override
    public void add(int vectid, String[] comps, float[] distr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(int vecid, float[] distr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(int vecid, String[] comps, float[] distr) {
        // TODO Auto-generated method stub

    }

    @Override
    public void accumulate(int vecid, float[] distr) {
        // TODO Auto-generated method stub

    }

    @Override
    public float[] get(int vecid) {
        float[] res = null;

        if (!vecidToidx.containsKey(vecid)) {
            return null;
        }
        int idx = vecidToidx.get(vecid);
        int size = getSlotSize(idx);
        res = new float[size];
        for (int i = 0; i < size; i++) {
            res[i] = hive.get(idx + i);
        }
        return res;
    }

    @Override
    public void remove(int vecid) {
        if (!vecidToidx.containsKey(vecid)) {
            return;
        }
        int idx = vecidToidx.get(vecid);
        vecidToidx.remove(vecid);
        discardSlot(idx);
    }
}
