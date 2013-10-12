package com.guokr.simbase.recommend;

import gnu.trove.map.hash.TIntFloatHashMap;

import com.guokr.simbase.SimContext;

public class Recommendation {
	private int limit;
	private TIntFloatHashMap items;
	private float waterline;

	public Recommendation(SimContext simContext) {
		limit = simContext.getInt("limit");
		items = new TIntFloatHashMap();
		waterline = 0;
	}

	void setLimit(int v) {
		limit = v;
	}
	
	float getWaterline() {
		return waterline;
	}

	/*
	 * score越高，表示越相近，waterline是当前门槛
	 */
	public void add(int vecid, float score) {
		if (score > waterline && items.size() < limit) {
			items.put(vecid, score);
			return;
		}

		if (score > waterline && items.size() == limit) {
			int[] ids = items.keys();
			float min = score;
			int minId = -1;
			for (int id : ids) {
				float v = items.get(id);
				if (v < min) {
					min = v;
					minId = id;
				}
			}

			if (minId >= 0) {
				items.remove(minId);
				items.put(vecid, score);
				waterline = min; // 第21个作为水位线
			}
			return;
		}
	}

	public void remove(int vecid) {
		items.remove(vecid);
	}

	public int[] ids() {
		return items.keys();
	}

	public String jsonize(int vecid) {
		return null;
	}
}
