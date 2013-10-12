package com.guokr.simbase.store;

import java.util.Map;

public class VectorSet {

	private Map<String, Object> config;
	private Basis base;

	public VectorSet(Map<String, Object> config, Basis base) {
		this.config = config;
		this.base = base;
	}

	public float[] get(int vecid) {
		return null;
	}

	public String retrive(int vecid) {
		return null;
	}

	public void remove(int vecid) {
	}

	// --------------------------------
	// Methods for dense vector inputs
	// --------------------------------

	public void add(int vecid, float[] distr) {
	}

	public void set(int vecid, float[] distr) {
	}

	public void accumulate(int vecid, float[] distr) {
	}

	// -----------------------------
	// Methods for json-like inputs
	// -----------------------------

	public void add(int vecid, String jsonlike) {
	}

	public void set(int vecid, String jsonlike) {
	}

	public void accumulate(int vecid, String jsonlike) {
	}

	// ------------------------------------------
	// Internal methods for sparse vector inputs
	// ------------------------------------------

	void _add(int vecid, int[] pairs) {
	}

	void _set(int vecid, int[] pairs) {
	}

	void _accumulate(int vecid, int[] pairs) {
	}

}
