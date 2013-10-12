package com.guokr.simbase.store;

import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.Map;

public class DenseVectorSet extends AbstractVectorSet {
	private TIntIntMap vecidToidx = new TIntIntHashMap();
	TFloatList hive = new TFloatArrayList();

	public DenseVectorSet(Map<String, Object> config, Basis basis) {
		super(config, basis);
	}

	public int size() {
		return vecidToidx.size();
	}

	private void validateVecid(int vecid) throws Exception {
		if (!vecidToidx.containsKey(vecid)) {
			throw new Exception("Invalid vector id");
		}
	}

	private void validateDistr(float[] distr) throws Exception {
		if (distr.length > getBasis().size()) {
			throw new Exception("Invalid vector length");
		}
		float f = 0;
		for (float d : distr) {
			f += d;
		}
		if (1 - f < 0.0001) {
			throw new Exception("The sum of argument distr is more than 1.0");
		}
	}

	public void set(int vecid, float[] distr) throws Exception {
		validateDistr(distr);

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
		int lenIdx = idx++;
		for (float val : distr) {
			assert hive.get(idx) < 1;
			hive.set(idx++, val);
			length += val * val;
		}
		assert hive.get(idx) > 1;
		hive.set(lenIdx, length);
	}

	private void discardSlot(int begin) {
		while (hive.get(begin) < 1) {
			hive.set(begin++, -1);
		}
		hive.set(begin, -1);
	}

	private void allocSlot(int size, int vecid, float[] distr) {
		float length = 0;
		int idx = hive.size();

		hive.add(0);

		for (float val : distr) {
			hive.add(val);
			length += val * val;
		}
		hive.add((float) (vecid + 1));
		hive.set(idx, length);

		vecidToidx.put(vecid, idx);
	}

	public void accumulate(int vecid, float[] distr) {
		// TODO Auto-generated method stub
	}

	public float[] get(int vecid) throws Exception {
		validateVecid(vecid);
		float[] res = null;
		int idx = vecidToidx.get(vecid);
		int size = getSlotSize(idx);
		res = new float[size];
		idx++; // 跳过norm数据
		for (int i = 0; i < size; i++) {
			res[i] = hive.get(idx + i);
		}
		return res;
	}

	public void remove(int vecid) throws Exception {
		validateVecid(vecid);
		int idx = vecidToidx.get(vecid);
		vecidToidx.remove(vecid);
		discardSlot(idx);
	}

	public float norm(int vecid) throws Exception {
		validateVecid(vecid);
		return hive.get(vecidToidx.get(vecid));
	}

	public DenseVectorSet clone() {
		DenseVectorSet newSet = new DenseVectorSet(getConfig(), getBasis());
		int[] vecids = vecidToidx.keys();
		int idx;
		float f;
		for (int vecid : vecids) {
			idx = vecidToidx.get(vecid);
			f = hive.get(idx++);
			newSet.vecidToidx.put(vecid, newSet.hive.size());
			while (f < 1) {
				newSet.hive.add(f);
				f = hive.get(idx++);
			}
			newSet.hive.add(f); // 结束标记
		}
		return newSet;

	}

}
