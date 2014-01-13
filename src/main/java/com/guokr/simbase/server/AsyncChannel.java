package com.guokr.simbase.server;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import sun.misc.Unsafe;
import clojure.lang.IFn;

public class AsyncChannel {
    static final Unsafe        unsafe;
    static final long          closedRanOffset;
    static final long          closeHandlerOffset;
    static final long          receiveHandlerOffset;
    static final long          headerSentOffset;

    private final SelectionKey key;
    private final SimServer    server;

    @SuppressWarnings("unused")
    private RedisRequests      requests;

    volatile int               closedRan      = 0;

    private volatile int       isHeaderSent   = 0;

    private volatile IFn       receiveHandler = null;
    volatile IFn               closeHandler   = null;

    static {
        try {
            // Unsafe instead of AtomicReference to save few bytes of RAM per
            // connection
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);

            closedRanOffset = unsafe.objectFieldOffset(AsyncChannel.class.getDeclaredField("closedRan"));
            closeHandlerOffset = unsafe.objectFieldOffset(AsyncChannel.class.getDeclaredField("closeHandler"));
            receiveHandlerOffset = unsafe.objectFieldOffset(AsyncChannel.class.getDeclaredField("receiveHandler"));
            headerSentOffset = unsafe.objectFieldOffset(AsyncChannel.class.getDeclaredField("isHeaderSent"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AsyncChannel(SelectionKey key, SimServer server) {
        this.key = key;
        this.server = server;
    }

    public void reset(RedisRequests requests) {
        this.requests = requests;
        unsafe.putOrderedInt(this, closedRanOffset, 0);
        unsafe.putOrderedInt(this, headerSentOffset, 0);
        unsafe.putOrderedObject(this, closeHandlerOffset, null);
        unsafe.putOrderedObject(this, receiveHandlerOffset, null);
    }

    private void firstWrite(Object data, boolean close) throws IOException {
        ByteBuffer buffers[] = null;
        if (close) {
            onClose(0);
        }
        server.tryWrite(key, buffers);
    }

    private void writeChunk(Object body, boolean close) throws IOException {
        if (close) {
            serverClose(0);
        }
    }

    public void setReceiveHandler(IFn fn) {
        if (!unsafe.compareAndSwapObject(this, receiveHandlerOffset, null, fn)) {
            throw new IllegalStateException("receive handler exist: " + receiveHandler);
        }
    }

    public void messageReceived(final Object mesg) {
        IFn f = receiveHandler;
        if (f != null) {
            f.invoke(mesg); // byte[] or String
        }
    }

    public void setCloseHandler(IFn fn) {
        if (!unsafe.compareAndSwapObject(this, closeHandlerOffset, null, fn)) {
            throw new IllegalStateException("close handler exist: " + closeHandler);
        }
        if (closedRan == 1) { // no handler, but already closed
            // fn.invoke(K_UNKNOWN);
        }
    }

    public void onClose(int status) {
        if (unsafe.compareAndSwapInt(this, closedRanOffset, 0, 1)) {
            IFn f = closeHandler;
            if (f != null) {
                // f.invoke(readable(status));
            }
        }
    }

    // also sent CloseFrame a final Chunk
    public boolean serverClose(int status) {
        if (!unsafe.compareAndSwapInt(this, closedRanOffset, 0, 1)) {
            return false; // already closed
        }
        // server.tryWrite(key, ByteBuffer.wrap(finalChunkBytes));
        IFn f = closeHandler;
        if (f != null) {
            // f.invoke(readable(0)); // server close is 0
        }
        return true;
    }

    public boolean send(Object data, boolean close) throws IOException {
        if (closedRan == 1) {
            return false;
        }

        if (isHeaderSent == 1) { // HTTP Streaming
            writeChunk(data, close);
        } else {
            isHeaderSent = 1;
            firstWrite(data, close);
        }

        return true;
    }

    public String toString() {
        Socket s = ((SocketChannel) key.channel()).socket();
        return s.getLocalSocketAddress() + "<->" + s.getRemoteSocketAddress();
    }

    public boolean isClosed() {
        return closedRan == 1;
    }

}
