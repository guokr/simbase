package com.guokr.simbase.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.guokr.simbase.SimRequest;

public class RedisRequests implements Iterable<SimRequest> {

    public AsyncChannel      channel;
    public InetSocketAddress remoteAddr;

    private List<SimRequest> list = new ArrayList<SimRequest>();
    private SimRequest       last;

    public RedisRequests() {
    }

    public void request(int size) {
        last = new SimRequest(size);
        list.add(last);
    }

    public void add(String s) {
        last.add(s);
    }

    @Override
    public Iterator<SimRequest> iterator() {
        return list.iterator();
    }

    public void reset() {
        list.clear();
    }

}
