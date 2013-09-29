package com.guokr.simbase.store;


public class Recommendation implements IRecommendation {
	private int recNum;

	public Recommendation(int recNum) {
		this.recNum = recNum;
	}

	@Override
	public int[] recommends(int vecid) {
		return null;
	}

	@Override
	public int[] range(float score, float delta) {
		return null;
	}

	@Override
	public void add(int vecid, float profile) {

	}
}
