package com.guokr.simbase.store;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guokr.simbase.events.BasisListener;

public class Basis {
    private String[]             base;
    private List<String>         schema;
    private Map<String, Integer> compIndex;
    private List<BasisListener>  listeners;

    public Basis() {
        this.base = new String[0];
        this.schema = new ArrayList<String>();
        this.compIndex = new HashMap<String, Integer>();
        this.listeners = new ArrayList<BasisListener>();
    }

    public Basis(String[] comps) {
        this.base = comps.clone();
        this.schema = new ArrayList<String>(Arrays.asList(comps));
        this.compIndex = new HashMap<String, Integer>();
        int index = 0;
        for (String comp : this.schema) {
            this.compIndex.put(comp, index++);
        }
        this.listeners = new ArrayList<BasisListener>();
    }

    public String[] all() {
        String[] result = new String[this.schema.size()];
        int index = 0;
        for (String comp : this.schema) {
            result[index++] = comp;
        }
        return result;
    }

    public int total() {
        return this.schema.size();
    }

    public String[] get() {
        return this.base;
    }

    public int pos(String comp) {
        return this.compIndex.get(comp);
    }

    public int size() {
        return this.base.length;
    }

    public void revise(String[] base) {
        for (String dim : base) {
            if (!this.compIndex.containsKey(dim)) {
                this.schema.add(dim);
                this.compIndex.put(dim, this.schema.size());
            }
        }
        String[] old = this.base;
        this.base = base.clone();
        
        for(BasisListener l: listeners) {
            l.onBasisRevised(this, old, base);
        }
    }

    float[] densify(int sparseFactor, int[] pairs) {
        int size = size();
        float[] result = new float[size];

        int index = 0, cursor = 0;
        while (cursor < size) {
            if (cursor == pairs[index]) {
                result[cursor] = ((float) pairs[index + 1]) / sparseFactor;
                index = index + 2;
            } else {
                result[cursor] = 0f;
            }
            cursor++;
        }

        return result;
    }

    int[] sparsify(int sparseFactor, float[] distr) {
        TIntArrayList resultList = new TIntArrayList();
        float ftmp = 0;
        int cursor = 0;
        while ((ftmp = distr[cursor++]) >= 0 && (ftmp < 1)) {
            int itmp = (int) ftmp * sparseFactor;
            if (itmp > 0) {
                resultList.add(cursor);
                resultList.add(itmp);
            }
        }
        int[] result = new int[resultList.size()];
        resultList.toArray(result);
        return result;
    }

    public void addListener(BasisListener listener) {
        this.listeners.add(listener);
    }
}
