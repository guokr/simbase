package com.guokr.simbase.server;

import com.guokr.simbase.SimCallback;

public class PipedCallback extends SimCallback {

    private int            order;
    private ServerCallback root;

    public PipedCallback(int order, ServerCallback root) {
        this.order = order;
        this.root = root;
    }

    public void response() {
        if (buffer == null) {
            error("Unknown server error!");
        }
        buffer.flip();

        root.buffers[order] = buffer;
        root.response();
    }

}
