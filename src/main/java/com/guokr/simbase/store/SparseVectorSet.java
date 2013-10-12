package com.guokr.simbase.store;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.Map;

/*
 * 多维向量集合的稀疏实现
 * 向量必须为整数向量，所有向量依次存放在int数组中
 * int数组二进制结构：
 * [维度个数][向量长度][维度编号] [相应维度数值] ...重复前两项
 */
public class SparseVectorSet extends AbstractVectorSet {
	private TIntIntMap vecidToidx = new TIntIntHashMap();
	TIntArrayList hive = new TIntArrayList();

	public SparseVectorSet(Map<String, Object> config, Basis base) {
		super(config, base);
	}

	public int size() {
		return vecidToidx.size();
	}

	public int norm(int vecid) throws Exception {
		int idx = vecidToIdx(vecid);
		return hive.get(idx + 1);
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
			int len = getVectorDimNum(vecid);
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
		int vectlen = 0;
		int vectlenIdx = 0;

		hive.set(idx++, pair.length / 2); // 有可能变小
		vectlenIdx = idx++;
		for (int i = 0; i < pair.length; i += 2) {
			hive.set(idx++, pair[i]);
			hive.set(idx++, pair[i + 1]);
			vectlen += pair[i + 1] * pair[i + 1];
		}
		hive.set(vectlenIdx, vectlen);
	}

	private void allocVector(int vecid, int[] pair) throws Exception {
		int idx = hive.size();
		int len = pair.length / 2;
		int vectlenIdx = 0;
		int vectlen = 0;

		hive.add(len);
		hive.add(0);
		vectlenIdx = hive.size() - 1;
		for (int i = 0; i < pair.length; i++) {
			hive.add(pair[i]);
			if (i % 2 == 1) {
				vectlen += pair[i] * pair[i];
			}
		}
		hive.set(vectlenIdx, vectlen);
		vecidToidx.put(vecid, idx);
	}

	private void discardVector(int vecid, int idx) throws Exception {
		int len = getVectorDimNum(vecid);

		hive.set(idx++, Integer.MIN_VALUE); // 清除计数
		hive.set(idx, Integer.MIN_VALUE); // 清除长度

		for (int i = idx + 1; i < idx + 2 * len + 1; i++) {
			hive.set(i++, Integer.MIN_VALUE); // 维度字段
			hive.set(i, Integer.MIN_VALUE); // 维度数据
		}
		vecidToidx.remove(vecid);
	}

	private int getVectorDimNum(int vecid) throws Exception {
		int idx = vecidToIdx(vecid);
		return hive.get(idx);
	}

	public void accumulate(int vecid, int[] distr) {
		// TODO Auto-generated method stub

	}

	public int[] get(int vecid) {
		int len, idx;
		try {
			len = getVectorDimNum(vecid);
			idx = vecidToIdx(vecid);
		} catch (Exception e) {
			return null;
		}

		int[] res = new int[getBasis().size()];
		for (int i = idx + 2; i < idx + 2 * len + 2; i++) {
			int dim = hive.get(i++);
			int d = hive.get(i);
			res[dim] = d;
		}
		return res;
	}

	public void remove(int vecid) {
		try {
			int idx = vecidToIdx(vecid);
			int len = getVectorDimNum(vecid);

			vecidToidx.remove(vecid);
			for (int i = idx; i < idx + 2 * len + 2; i++) {
				hive.set(i, Integer.MIN_VALUE);
			}
		} catch (Exception e) {
			return;
		}
	}

	public SparseVectorSet clone() {
		SparseVectorSet newSet = new SparseVectorSet(this.getConfig(),
				this.getBasis());
		int[] vecids = vecidToidx.keys();
		int idx;
		int dimNum;
		int len;
		int i;
		for (int vecid : vecids) {
			idx = vecidToidx.get(vecid);
			dimNum = hive.get(idx++);
			len = hive.get(idx++);

			newSet.vecidToidx.put(vecid, newSet.hive.size());
			newSet.hive.add(dimNum);
			newSet.hive.add(len);
			for (i = 0; i < dimNum; i++) {
				newSet.hive.add(hive.get(idx++));
				newSet.hive.add(hive.get(idx++));
			}
		}

		return newSet;
	}
}
