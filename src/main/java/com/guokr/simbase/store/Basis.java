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

    String                       key;

    List<String>                 schema;

    private Map<String, Integer> compIndex;
    private List<BasisListener>  listeners;

    public Basis(String key) {
        this.key = key;
        this.schema = new ArrayList<String>();
        this.compIndex = new HashMap<String, Integer>();
        this.listeners = new ArrayList<BasisListener>();
    }

    public Basis(String key, String[] comps) {
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
        this.key = key;
        this.schema = schema;
        this.compIndex = compIndex;
        this.listeners = new ArrayList<BasisListener>();
    }

    public String key() {
        return key;
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
        return this.schema.toArray(new String[this.schema.size()]);
    }

    public int pos(String comp) {
        return this.compIndex.get(comp);
    }

    public int size() {
        return this.schema.size();
    }

    public void revise(String[] base) {
        String[] old = get();

        for (String dim : base) {
            if (!this.compIndex.containsKey(dim)) {
                this.schema.add(dim);
                this.compIndex.put(dim, this.schema.size());
            }
        }

        for (BasisListener l : listeners) {
            l.onBasisRevised(this, old, base);
        }
    }

    public static void densify(int size, int sparseFactor, int[] pairs, float[] result) {
        int length = pairs.length;
        int index = 0, cursor = 0;
        while (cursor < size) {
            if (index < length && cursor == pairs[index]) {
                float val = (Math.round(((float) pairs[index + 1]) / sparseFactor * 1000)) / 1000f;
                result[cursor] = val;
                index = index + 2;
            } else {
                result[cursor] = 0f;
            }
            cursor++;
        }
    }

    public static float[] densify(int size, int sparseFactor, int[] pairs) {
        float[] result = new float[size];
        densify(size, sparseFactor, pairs, result);
        return result;
    }

    public static void sparsify(int sparseFactor, float[] distr, int[] result) {
        int cursor = 0, idx = 0, length = result.length;
        for (float ftmp : distr) {
            int itmp = Math.round(ftmp * sparseFactor);
            if (itmp != 0) {
                result[idx] = cursor;
                result[idx + 1] = itmp;
            }
            cursor++;
            idx = idx + 2;
        }
        for (int i = idx; i < length;) {
            result[i++] = -1;
            result[i++] = 0;
        }
    }

    public static int[] sparsify(int sparseFactor, float[] distr) {
        TIntArrayList resultList = new TIntArrayList();

        int cursor = 0;
        for (float ftmp : distr) {
            int itmp = Math.round(ftmp * sparseFactor);
            if (itmp != 0) {
                resultList.add(cursor);
                resultList.add(itmp);
            }
            cursor++;
        }

        return resultList.toArray();
    }

    public void addListener(BasisListener listener) {
        this.listeners.add(listener);
    }
}
