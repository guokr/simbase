package com.guokr.simbase.server;

import java.nio.ByteBuffer;

import com.guokr.simbase.SimCallback;

public class ServerCallback extends SimCallback {

    public RespCallback response;
    public ByteBuffer[] buffers;

    private int         pipesize;

    public ServerCallback(int size, RespCallback cb) {
        this.buffers = new ByteBuffer[size];
        this.response = cb;
        this.pipesize = size;
    }

    public void response() {
        if (pipesize == 1) {
            response.run(buffers);
        } else {
            pipesize--;
        }
    }

}
