package com.guokr.simbase.server;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class RespCallback {
    private final SelectionKey key;
    private final SimServer server;

    public RespCallback(SelectionKey key, SimServer server) {
        this.key = key;
        this.server = server;
    }

    // maybe in another thread :worker thread
    public void run(ByteBuffer... buffers) {
        server.tryWrite(key, buffers);
    }
}
