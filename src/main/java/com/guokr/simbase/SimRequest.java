package com.guokr.simbase;

import java.util.ArrayList;
import java.util.List;

public class SimRequest {

    private List<Object> content = new ArrayList<Object>();
    private int          size;
    private int          toberead;
    private List<Object> last;

    public SimRequest(int size) {
        this.size = size;
    }

    public String name() {
        if (content.size() > 0) {
            return content.get(0).toString();
        } else {
            return "";
        }
    }

    public int argsize() {
        return size - 1;
    }

    public void list(int size) {
        if (toberead != 0) {
            throw new IllegalStateException("reading for previous argument not finished");
        }
        toberead = size;
        last = new ArrayList<Object>();
        content.add(last);
    }

    public void add(Object arg) {
        if (content.size() < size) {
            if (toberead == 0) {
                content.add(arg);
            } else {
                last.add(arg);
                toberead--;
            }
        }
    }

    public Object arg(int idx) {
        return content.get(idx);
    }

    public int argint(int idx) {
        return (int)content.get(idx);
    }

    public float argfloat(int idx) {
        return (float)content.get(idx);
    }

    public String argstring(int idx) {
        return (String)content.get(idx);
    }

    public int[] argarrayint(int idx) {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) content.get(idx);
        int len = list.size();
        int[] result = new int[list.size()];
        for (int i = 0; i < len; i++) {
            result[i] = (int)list.get(i);
        }
        return result;
    }

    public float[] argarrayfloat(int idx) {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) content.get(idx);
        int len = list.size();
        float[] result = new float[list.size()];
        for (int i = 0; i < len; i++) {
            result[i] = (float)list.get(i);
        }
        return result;
    }

    public String[] argarraystring(int idx) {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) content.get(idx);
        int len = list.size();
        String[] result = new String[list.size()];
        for (int i = 0; i < len; i++) {
            result[i] = (String)list.get(i);
        }
        return result;
    }

}
