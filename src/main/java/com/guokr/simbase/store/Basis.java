package com.guokr.simbase.store;

import java.util.HashMap;
import java.util.Map;

public class Basis {
	private int size;
	private String[] comps;
	Map<String, Integer> compIndex;

	public Basis() {

	}

	public Basis(String[] comps) {
		size = comps.length;
		this.comps = comps;
		compIndex = new HashMap<String, Integer>();
		int index = 0;
		for (String comp : comps) {
			compIndex.put(comp, index++);
		}
	}

	public String[] get() {
		return comps.clone();
	}

	public int pos(String comp) {
		return compIndex.get(comp);
	}

	public int size() {
		return size;
	}

	public void revise(String[] base) {
	}
}
