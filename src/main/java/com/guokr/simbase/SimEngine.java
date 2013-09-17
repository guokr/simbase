package com.guokr.simbase;

import java.util.Map;

public class SimEngine {

	private Map<String, Object> context;

	public SimEngine(Map<String, Object> context) {
		this.context = context;
	}
	
	public void del(String key) {
	}

	public int xincr(String key) {
		return 1;
	}

	public String xget(int vecid) {
		return null;
	}

	public String[] blist() {
		return null;
	}

	public void bmk(String bkey, String[] base) {
	}

	public void brev(String bkey, String[] base) {
	}

	public String[] bget(String bkey) {
		return null;
	}

	public String[] vlist(String bkey) {
		return null;
	}

	public void vmk(String vkey, String bkey) {
	}

	public void vadd(String vkey, int vecid, float[] distr) {
	}

	public void vacc(String vkey, int vecid, float[] distr) {
	}

	public void vrem(String vkey, int vecid) {
	}

	public float[] vget(String vkey, int vecid) {
		return null;
	}

	public String[] rlist(String vkey) {
		return null;
	}

	public void rmk(String vkeySource, String vkeyTarget) {
	}

	public String[] rget(String vkeySource, String vkeyTarget) {
		return null;
	}

	public int[] rrec(String vkeySource, String vkeyTarget) {
		return null;
	}

}
