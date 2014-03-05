package com.guokr.simbase.server;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import com.guokr.simbase.SimCallback;

public class ServerCallback extends SimCallback {

    public RespCallback   response;
    public ByteBuffer[]   buffers;

    private AtomicInteger pipesize;

    public ServerCallback(int size, RespCallback cb) {
        this.buffers = new ByteBuffer[size];
        this.response = cb;
        this.pipesize = new AtomicInteger(size);
    }

    public void response() {
        if (pipesize.intValue() == 1) {
            response.run(buffers);
        } else {
            pipesize.decrementAndGet();
        }
    }

}
