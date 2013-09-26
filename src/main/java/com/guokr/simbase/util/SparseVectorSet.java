package com.guokr.simbase.util;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.Collection;
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

    public int size() {
        return vecidToidx.size();
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

    private void validatePair(int[] pair) throws Exception {
        if (pair.length % 2 != 0) {
            throw new Exception("invalid vector pair length.");
        }

        if (pair.length / 2 > getBasis().size()) {
            throw new Exception("invalid vector size.");
        }
        for (int i = 0; i < pair.length; i += 2) {
            if (pair[i] >= getBasis().size() || pair[i] < 0) {
                throw new Exception("vector index beyond the basis size.");
            }
        }
    }

    public void set(int vecid, int[] pair) throws Exception {
        validatePair(pair);

        if (vecidToidx.containsKey(vecid)) {
            int idx = vecidToIdx(vecid);
            int len = getVectorLength(vecid);
            if (len < pair.length / 2) {
                discardVector(vecid, idx);
                allocVector(vecid, pair);
            } else {
                updateVector(vecid, pair);
            }
        } else {
            allocVector(vecid, pair);
        }
    }

    private void updateVector(int vecid, int[] pair) throws Exception {
        /*
         * 私有接口不再检查参数
         */
        int idx = vecidToIdx(vecid);

        hive.set(idx++, pair.length / 2); // 有可能变小
        for (int i = 0; i < pair.length; i += 2) {
            hive.set(idx++, pair[i]);
            hive.set(idx++, pair[i + 1]);
        }
    }

    private void allocVector(int vecid, int[] pair) throws Exception {
        int idx = hive.size();
        int len = pair.length / 2;

        hive.add(len);
        for (int d : pair) {
            hive.add(d);
        }
        vecidToidx.put(vecid, idx);
    }

    private void discardVector(int vecid, int idx) throws Exception {
        int len = getVectorLength(vecid);

        hive.set(idx, Integer.MIN_VALUE); // 长度字段本身

        for (int i = idx + 1; i < idx + 2 * len + 1; i++) {
            hive.set(i++, Integer.MIN_VALUE); // 维度字段
            hive.set(i, Integer.MIN_VALUE); // 维度数据
        }
        vecidToidx.remove(vecid);
    }

    private int getVectorLength(int vecid) throws Exception {
        int idx = vecidToIdx(vecid);
        return hive.get(idx);
    }

    public void accumulate(int vecid, int[] distr) {
        // TODO Auto-generated method stub

    }

    public int[] get(int vecid) {
        int len, idx;
        try {
            len = getVectorLength(vecid);
            idx = vecidToIdx(vecid);
        } catch (Exception e) {
            return null;
        }

        int[] res = new int[getBasis().size()];
        for (int i = idx + 1; i < idx + 2 * len + 1; i++) {
            int dim = hive.get(i++);
            int d = hive.get(i);
            res[dim] = d;
        }
        return res;
    }

    public void remove(int vecid) {
        try {
            int idx = vecidToIdx(vecid);
            int len = getVectorLength(vecid);

            vecidToidx.remove(vecid);
            for (int i = idx; i < idx + 2 * len + 1; i++) {
                hive.set(i, Integer.MIN_VALUE);
            }
        } catch (Exception e) {
            return;
        }
    }
}
