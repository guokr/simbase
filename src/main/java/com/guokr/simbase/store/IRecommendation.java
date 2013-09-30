package com.guokr.simbase.store;

public interface IRecommendation {

	/*
	 * 返回与某向量相关的id数组
	 */
	public int[] recommends(int vecid);

	/*
	 * 返回评分与某分数接近的id数组
	 */
	public int[] range(float score, float delta);

	/*
	 * 添加一个item
	 */
	public void add(int vecid, float profile);
}
