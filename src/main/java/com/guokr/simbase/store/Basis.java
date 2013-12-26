package com.guokr.simbase.store;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guokr.simbase.errors.SimEngineException;
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
        List<String> schema = new ArrayList<String>(Arrays.asList(comps));
        Map<String, Integer> compIndex = new HashMap<String, Integer>();
        int index = 0;
        for (String comp : schema) {
            if (compIndex.containsKey(comp)) {
                throw new SimEngineException(String.format("Dupicate base schema '%s'", comp));
            } else {
                compIndex.put(comp, index++);
            }
        }
        this.base = comps.clone();
        this.schema = schema;
        this.compIndex = compIndex;
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
        String[] newbase = new String[this.schema.size()];
        int pos = 0;
        for (String dim : this.schema) {
            newbase[pos++] = dim;
        }
        this.base = newbase;

        for (BasisListener l : listeners) {
            l.onBasisRevised(this, old, base);
        }
    }

    float[] densify(int sparseFactor, int[] pairs) {
        int size = size();
        int length = pairs.length;
        float[] result = new float[size];

        int index = 0, cursor = 0;
        float sum = 0f;
        while (cursor < size) {
            if (index < length && cursor == pairs[index]) {
                float val = ((float) pairs[index + 1]) / sparseFactor;
                result[cursor] = val;
                sum = sum + val;
                index = index + 2;
            } else {
                result[cursor] = 0f;
            }
            cursor++;
        }

        if (sum > 0f) {
            for (int i = 0; i < size; i++) {
                result[i] = result[i] / sum;
            }
        } else {
            for (int i = 0; i < size; i++) {
                result[i] = 1.0f / size;
            }
        }

        return result;
    }

    int[] sparsify(int sparseFactor, float[] distr) {
        TIntArrayList resultList = new TIntArrayList();

        int cursor = 0, sum = 0;
        for (float ftmp : distr) {
            int itmp = Math.round(ftmp * sparseFactor);
            if (itmp > 0) {
                resultList.add(cursor);
                resultList.add(itmp);
                sum += itmp;
            }
            cursor++;
        }

        int size = resultList.size();
        int[] result = new int[size];
        for (int i = 0; i < size;) {
            result[i] = resultList.get(i);
            result[i + 1] = (int) (((float) resultList.get(i + 1)) / sum * sparseFactor);
            i += 2;
        }

        return result;
    }

    public void addListener(BasisListener listener) {
        this.listeners.add(listener);
    }
}
