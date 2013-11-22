package com.guokr.simbase;

import java.util.ArrayList;
import java.util.List;

public class SimRequest {

    private List<Object> content = new ArrayList<Object>();
    private int          size;
    private int          toberead;
    private List<Object> last;

    public void string(int size) {
        if (toberead != 0) {
            throw new IllegalStateException("reading for previous argument not finished");
        }
        toberead = size;
    }

    public void intlist(int size) {
        if (toberead != 0) {
            throw new IllegalStateException("reading for previous argument not finished");
        }
        toberead = size;
        last = new ArrayList<Object>();
        content.add(last);
    }

    public void floatlist(int size) {
        if (toberead != 0) {
            throw new IllegalStateException("reading for previous argument not finished");
        }
        toberead = size;
        last = new ArrayList<Object>();
        content.add(last);
    }

    public SimRequest(int size) {
        this.size = size;
    }

    public void add(int arg) {
        if (content.size() < size) {
            if (toberead == 0) {
                content.add(arg);
            } else {
                last.add(arg);
                toberead--;
            }
        }
    }

    public void add(float arg) {
        if (content.size() < size) {
            if (toberead == 0) {
                content.add(arg);
            } else {
                last.add(arg);
                toberead--;
            }
        }
    }

    public void add(String arg) {
        if (content.size() < size) {
            content.add(arg);
            toberead = toberead - arg.length();
        }
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

    public Object arg(int idx) {
        return content.get(idx);
    }

}
