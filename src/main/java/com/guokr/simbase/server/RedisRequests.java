package com.guokr.simbase.server;

import java.net.InetSocketAddress;
import java.util.Iterator;

import com.guokr.simbase.SimRequest;

public class RedisRequests implements Iterable<SimRequest> {

    InetSocketAddress remoteAddr;
    AsyncChannel      channel;

    public RedisRequests() {
    }

    public void request(int size) {
    }

    public void string(int size) {
    }

    public void intarray(int size) {
    }

    public void floatarray(int size) {
    }

    public void arrayadd(int num) {
    }

    public void arrayadd(float num) {
    }

    public void set(String string) {
    }

    @Override
    public Iterator<SimRequest> iterator() {
        return null;
    }

}
