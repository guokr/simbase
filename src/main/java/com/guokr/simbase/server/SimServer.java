package com.guokr.simbase.server;

import static com.guokr.simbase.server.Frame.CloseFrame.CLOSE_AWAY;
import static com.guokr.simbase.server.Frame.CloseFrame.CLOSE_NORMAL;
import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.guokr.simbase.SimUtils;
import com.guokr.simbase.SimContext;
import com.guokr.simbase.errors.server.ProtocolException;

public class SimServer implements Runnable {

    static final String                               THREAD_NAME = "server-loop";

    private final IHandler                            handler;

    private final Selector                            selector;
    private final ServerSocketChannel                 serverChannel;

    private Thread                                    serverThread;

    private final ConcurrentLinkedQueue<SelectionKey> pending     = new ConcurrentLinkedQueue<SelectionKey>();
    // shared, single thread
    private final ByteBuffer                          buffer      = ByteBuffer.allocateDirect(1024 * 64);

    public SimServer(String ip, int port, IHandler handler) throws IOException {
        this.handler = handler;
        this.selector = Selector.open();
        this.serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(ip, port));
        serverChannel.register(selector, OP_ACCEPT);
    }

    public SimServer(SimContext ctx, IHandler handler) throws IOException {
        this(ctx.getString("ip"), ctx.getInt("port"), handler);
    }

    void accept(SelectionKey key) {
        SimUtils.printTrace("accept incoming request");
        ServerSocketChannel ch = (ServerSocketChannel) key.channel();
        SocketChannel s;
        try {
            while ((s = ch.accept()) != null) {
                s.configureBlocking(false);
                RedisAtta atta = new RedisAtta();
                SelectionKey k = s.register(selector, OP_READ, atta);
                atta.channel = new AsyncChannel(k, this);
            }
        } catch (Exception e) {
            // too many open files. do not quit
            SimUtils.printError("accept incoming request", e);
        }
    }

    private void closeKey(final SelectionKey key, int status) {
        SimUtils.printTrace("close key");
        try {
            key.channel().close();
        } catch (Exception ignore) {
        }

        ServerAtta att = (ServerAtta) key.attachment();
        if (att instanceof RedisAtta) {
            handler.clientClose(att.channel, -1);
        } else {
            handler.clientClose(att.channel, status);
        }
    }

    private void decodeRedis(RedisAtta atta, SelectionKey key, SocketChannel ch) {
        try {
            do {
                AsyncChannel channel = atta.channel;
                RedisRequests requests = atta.decoder.decode(buffer);
                if (requests != null) {
                    channel.reset(requests);
                    requests.channel = channel;
                    requests.remoteAddr = (InetSocketAddress) ch.socket().getRemoteSocketAddress();
                    handler.handle(requests, new RespCallback(key, this));
                    // pipelining not supported : need queue to ensure order
                    // atta.decoder.reset();
                }
            } while (buffer.hasRemaining()); // consume all
        } catch (ProtocolException e) {
            SimUtils.printError("protocol exception", e);
            closeKey(key, -1);
        }
    }

    private void doRead(final SelectionKey key) {
        SimUtils.printTrace("reading");
        SocketChannel ch = (SocketChannel) key.channel();
        try {
            buffer.clear(); // clear for read
            int read = ch.read(buffer);
            if (read == -1) {
                // remote entity shut the socket down cleanly.
                closeKey(key, CLOSE_AWAY);
            } else if (read > 0) {
                buffer.flip(); // flip for read
                final ServerAtta atta = (ServerAtta) key.attachment();
                if (atta instanceof RedisAtta) {
                    decodeRedis((RedisAtta) atta, key, ch);
                }
            }
        } catch (IOException e) { // the remote forcibly closed the connection
            closeKey(key, CLOSE_AWAY);
        }
    }

    private void doWrite(SelectionKey key) {
        SimUtils.printTrace("writing");
        ServerAtta atta = (ServerAtta) key.attachment();
        SocketChannel ch = (SocketChannel) key.channel();
        try {
            // the sync is per socket (per client). virtually, no contention
            // 1. keep byte data order, 2. ensure visibility
            synchronized (atta) {
                LinkedList<ByteBuffer> toWrites = atta.toWrites;
                int size = toWrites.size();
                if (size == 1) {
                    ch.write(toWrites.get(0));
                    // TODO investigate why needed.
                    // ws request for write, but has no data?
                } else if (size > 0) {
                    ByteBuffer buffers[] = new ByteBuffer[size];
                    toWrites.toArray(buffers);
                    ch.write(buffers, 0, buffers.length);
                }
                Iterator<ByteBuffer> ite = toWrites.iterator();
                while (ite.hasNext()) {
                    if (!ite.next().hasRemaining()) {
                        ite.remove();
                    }
                }
                // all done
                if (toWrites.size() == 0) {
                    if (atta.isKeepAlive()) {
                        key.interestOps(OP_READ);
                    } else {
                        closeKey(key, CLOSE_NORMAL);
                    }
                }
            }
        } catch (IOException e) { // the remote forcibly closed the connection
            closeKey(key, CLOSE_AWAY);
        }
    }

    public void tryWrite(final SelectionKey key, ByteBuffer... buffers) {
        ServerAtta atta = (ServerAtta) key.attachment();
        synchronized (atta) {
            if (atta.toWrites.isEmpty()) {
                SocketChannel ch = (SocketChannel) key.channel();
                try {
                    // TCP buffer most of time is empty, writable(8K ~ 256k)
                    // One IO thread => One thread reading + Many thread writing
                    // Save 2 system call
                    ch.write(buffers, 0, buffers.length);
                    if (buffers[buffers.length - 1].hasRemaining()) {
                        for (ByteBuffer b : buffers) {
                            if (b.hasRemaining()) {
                                atta.toWrites.add(b);
                            }
                        }
                        pending.add(key);
                        selector.wakeup();
                    } else if (!atta.isKeepAlive()) {
                        closeKey(key, CLOSE_NORMAL);
                    }
                } catch (IOException e) {
                    closeKey(key, CLOSE_AWAY);
                }
            } else {
                // If has pending write, order should be maintained. (WebSocket)
                Collections.addAll(atta.toWrites, buffers);
                pending.add(key);
                selector.wakeup();
            }
        }
    }

    public void run() {
        while (true) {
            try {
                SelectionKey k = null;
                while ((k = pending.poll()) != null) {
                    if (k.isValid()) {
                        k.interestOps(OP_WRITE);
                    }
                }
                if (selector.select() <= 0) {
                    continue;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                for (SelectionKey key : selectedKeys) {
                    // TODO I do not know if this is needed -- shengfeng
                    // if !valid, isAcceptable, isReadable.. will Exception
                    // run hours happily after commented, but not sure.
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
                        doRead(key);
                    } else if (key.isWritable()) {
                        doWrite(key);
                    }
                }
                selectedKeys.clear();
            } catch (ClosedSelectorException ignore) {
                return; // stopped
                // do not exits the while IO event loop. if exits, then will not
                // process any IO event
                // jvm can catch any exception, including OOM
            } catch (Throwable e) { // catch any exception(including OOM), print
                                    // it
                SimUtils.printError("http server loop error, should not happen", e);
            }
        }
    }

    public void start() throws IOException {
        serverThread = new Thread(this, THREAD_NAME);
        serverThread.start();
    }

    public void stopAccept() {
        try {
            serverChannel.close(); // stop accept any request
        } catch (IOException ignore) {
        }
    }

    public void stop() {
        if (selector.isOpen()) {
            try {
                serverChannel.close();
                Set<SelectionKey> keys = selector.keys();
                for (SelectionKey k : keys) {
                    k.channel().close();
                }
                selector.close();
                handler.close(0);
            } catch (IOException ignore) {
            }
            serverThread.interrupt();
        }
    }
}
