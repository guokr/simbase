package com.guokr.simbase.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.guokr.simbase.SimRequest;
import com.guokr.simbase.server.RedisDecoder.State;

public class RedisRequests implements Iterable<SimRequest> {

    public AsyncChannel      channel;
    public InetSocketAddress remoteAddr;
    public boolean           isFinished;

    private List<SimRequest> list = new ArrayList<SimRequest>();

    private SimRequest       last;

    public State             state;
    public int               nargs;
    public int               nbytes;
    public String            line;
    public LineReader        lineReader;

    public RedisRequests() {
    }

    public void request(int size) {
        last = new SimRequest(size);
        list.add(last);
    }

    public void add(String s) {
        last.add(s);
    }

    public int length() {
        return list.size();
    }

    @Override
    public Iterator<SimRequest> iterator() {
        return list.iterator();
    }

    public void reset() {
        list.clear();
    }

}
