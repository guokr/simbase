package com.guokr.simbase.util;

import java.util.Map;

public class VectorSet {

	private Map<String, Object> config;
	private Base base;

	public VectorSet(Map<String, Object> config, Base base) {
		this.config = config;
		this.base = base;
	}

	public void add(int vecid, float[] distr) {
	}

	public void insert(int vecid, String[] comps, float[] distr) {
	}

	public void set(int vecid, float[] distr) {
	}

	public void update(int vecid, String[] comps, float[] distr) {
	}

	public void accumulate(int vecid, float[] distr) {
	}

	public float[] get(int vecid) {
		return null;
	}

	public String[] retrive(int vecid) {
		return null;
	}

	public void remove(int vecid) {
	}

}
