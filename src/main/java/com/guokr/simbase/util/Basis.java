package com.guokr.simbase.util;

import java.util.HashMap;
import java.util.Map;

public class Basis {
    private int size;
    private String[] comps;
    Map<String, Integer> compIndex;
    
    public Basis(String[] comps) {
        size = comps.length;
        this.comps = comps;
        compIndex = new HashMap<String, Integer>();
        int index = 0;
        for (String comp: comps) {
            compIndex.put(comp, index++);
        }
    }

	public String[] get() {
		return null;
	}

	public int pos(String comp) {
		return 0;
	}
	
	public int size() {
	    return size;
	}

	public void revise(String[] base) {
	}
	
	public String[] getComps() {
	    return comps.clone();
	}
	
	public boolean hasComp(String compName) {
	    return compIndex.get(compName) != null;
	}

    public int getCompIndex(String compName) {
        return compIndex.get(compName);
    }
}
