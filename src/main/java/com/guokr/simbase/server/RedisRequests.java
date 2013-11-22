package com.guokr.simbase.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.guokr.simbase.SimRequest;

public class RedisRequests implements Iterable<SimRequest> {

    public AsyncChannel      channel;
    public InetSocketAddress remoteAddr;

    private List<SimRequest>  list        = new ArrayList<SimRequest>();
    private SimRequest last; 

    public RedisRequests() {
    }

    public void request(int size) {
        last = new SimRequest(size);
        list.add(last);
    }

    public void string(int size) {
        last.string(size);
    }

    public void intarray(int size) {
        last.intlist(size);
    }

    public void floatarray(int size) {
        last.floatlist(size);
    }

    public void arrayadd(int num) {
        last.add(num);
    }

    public void arrayadd(float num) {
        last.add(num);
    }

    public void set(String string) {
        last.add(string);
    }

    @Override
    public Iterator<SimRequest> iterator() {
        return list.iterator();
    }

}
