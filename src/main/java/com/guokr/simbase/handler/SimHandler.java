package com.guokr.simbase.handler;

import com.guokr.simbase.engine.SimEngine;
import com.guokr.simbase.server.AsyncChannel;
import com.guokr.simbase.server.Frame;
import com.guokr.simbase.server.IHandler;
import com.guokr.simbase.server.RedisRequests;
import com.guokr.simbase.server.RespCallback;

public class SimHandler implements IHandler {

    public SimHandler(SimEngine engine) {
    }

    @Override
    public void handle(RedisRequests request, RespCallback callback) {
    }

    @Override
    public void handle(AsyncChannel channel, Frame frame) {
    }

    @Override
    public void clientClose(AsyncChannel channel, int status) {
    }

    @Override
    public void close(int timeoutMs) {
    }

}
