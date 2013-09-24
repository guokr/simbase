package com.guokr.simbase.util;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.Map;

import org.omg.CORBA.DynAnyPackage.Invalid;

/*
 * 多维向量集合的稀疏实现
 * 向量必须为整数向量，所有向量依次存放在int数组中
 * int数组二进制结构：
 * [向量长度] [维度编号] [相应维度数值] ...重复前两项
 */
public class SparseVectorSet extends AbstractVectorSet {
    private TIntIntMap vecidToidx = new TIntIntHashMap();
    TIntArrayList      hive       = new TIntArrayList();

    public SparseVectorSet(Map<String, Object> config, Basis base) {
        super(config, base);
    }

    private int vecidToIdx(int vecid) throws Exception {
        validateVecid(vecid);
        return vecidToidx.get(vecid);
    }

    private void validateVecid(int vecid) throws Exception {
        if (!vecidToidx.containsKey(vecid)) {
            throw new Exception("invalid vector id");
        }
    }

    private void validateDistr(float[] distr) throws Exception {
        if (distr.length > getBasis().size()) {
            throw new Exception("invalid vector size");
        }
    }

    @Override
    public void set(int vecid, float[] distr) throws Exception {
        validateDistr(distr);

        if (vecidToidx.containsKey(vecid)) {
            int idx = vecidToIdx(vecid);
            int len = getVectorLength(vecid);
            if (len < distr.length) {
                discardVector(vecid, idx);
                allocVector(vecid, distr);
            } else {
                updateVector(vecid, distr);
            }
        } else {
            allocVector(vecid, distr);
        }
    }

    private void updateVector(int vecid, float[] distr) throws Exception {
        validateDistr(distr);
        int idx = vecidToIdx(vecid);
        int len = getVectorLength(vecid);

        if (len < distr.length) {
            throw new Exception("Update failed.The argument distr's length is invalid");
        }

        hive.set(idx, distr.length); // 有可能变小
        int i = idx + 2;
        for (float f : distr) {
            hive.set(i, (int) (f * coefficient()));
            i += 2;
        }
    }

    private void allocVector(int vecid, float[] distr) throws Exception {
        validateDistr(distr);
        int idx = hive.size();
        int len = distr.length;

        hive.add(len);
        int dim = 0;
        for (float f : distr) {
            int d = (int) (f * coefficient());
            hive.add(dim++);
            hive.add(d);
        }
        vecidToidx.put(vecid, idx);
    }

    private int coefficient() {
        // TODO Auto-generated method stub
        return 100;
    }

    private void discardVector(int vecid, int idx) throws Exception {
        int len = getVectorLength(vecid);

        hive.set(idx++, Integer.MIN_VALUE); // 长度字段本身

        for (int i = idx; i < idx + 2 * len; i++) {
            hive.set(i++, Integer.MIN_VALUE); // 维度字段
            hive.set(i, Integer.MIN_VALUE); // 维度数据
        }
        vecidToidx.remove(vecid);
    }

    private int getVectorLength(int vecid) throws Exception {
        int idx = vecidToIdx(vecid);
        return hive.get(idx);
    }

    @Override
    public void add(int vectid, String[] comps, float[] distr) throws Exception {
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
        int len, idx;
        try {
            len = getVectorLength(vecid);
            idx = vecidToIdx(vecid);
        } catch (Exception e) {
            return null;
        }

        float[] res = new float[getBasis().size()];
        idx++;
        for (int i = idx; i < idx + 2 * len; i++) {
            int dim = hive.get(i++);
            int d = hive.get(i);
            res[dim] = (float) d / coefficient();
        }
        return res;
    }

    @Override
    public void remove(int vecid) {
        try {
            int idx = vecidToIdx(vecid);
            int len = getVectorLength(vecid);

            for (int i = idx; i < idx + 2 * len + 1; i++) {
                hive.set(i, Integer.MIN_VALUE);
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void update(int vecid, float[] distr) {
        // TODO Auto-generated method stub
    }
}
