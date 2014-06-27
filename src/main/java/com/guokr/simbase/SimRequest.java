package com.guokr.simbase;

import java.util.ArrayList;
import java.util.List;

public class SimRequest {

    private List<String> content = new ArrayList<String>();
    private int          size;

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

    public void add(String s) {
        if (content.size() < size) {
            content.add(s);
        }
    }

    public int argi(int idx) {
        return Integer.parseInt(content.get(idx));
    }

    public long argl(int idx) {
        return Long.parseLong(content.get(idx));
    }

    public float argf(int idx) {
        return Float.parseFloat(content.get(idx));
    }

    public String args(int idx) {
        return content.get(idx);
    }

    public int[] argI(int idx) {
        int len = this.content.size();
        int[] results = new int[len - idx];
        for (int i = idx; i < len; i++) {
            results[i - idx] = Integer.parseInt(this.content.get(i));
        }
        return results;
    }

    public long[] argL(int idx) {
        int len = this.content.size();
        long[] results = new long[len - idx];
        for (int i = idx; i < len; i++) {
            results[i - idx] = Long.parseLong(this.content.get(i));
        }
        return results;
    }

    public float[] argF(int idx) {
        int len = this.content.size();
        float[] results = new float[len - idx];
        for (int i = idx; i < len; i++) {
            results[i - idx] = Float.parseFloat(this.content.get(i));
        }
        return results;
    }

    public String[] argS(int idx) {
        int len = this.content.size();
        String[] results = new String[len - idx];
        for (int i = idx; i < len; i++) {
            results[i - idx] = this.content.get(i).toString();
        }
        return results;
    }

}
