package com.guokr.simbase.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.guokr.simbase.SimEngine;
import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimRegistry;
import com.guokr.simbase.SimRequest;
import com.guokr.simbase.SimUtils;

public class ServerHandler implements IHandler {
    private final ExecutorService execs;
    private final SimRegistry     registry;
    private final SimEngine       engine;

    public ServerHandler(int threadPoolSize, String prefix, int queueSize, SimRegistry registry, SimEngine engine) {
        this.registry = registry;
        this.engine = engine;
        PrefixThreadFactory factory = new PrefixThreadFactory(prefix);
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(queueSize);
        this.execs = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0, TimeUnit.MILLISECONDS, queue, factory);
    }

    public void handle(RedisRequests reqs, final RespCallback cb) {
        try {
            for (final SimRequest request : reqs) {
                execs.submit(new Runnable() {
                    @Override
                    public void run() {
                        SimCallback callback = new ServerCallback(cb);
                        switch (request.argsize()) {
                        case 0:
                            registry.get(request.name()).invoke(engine, callback);
                            break;
                        case 1:
                            registry.get(request.name()).invoke(engine, request.arg(0), callback);
                            break;
                        case 2:
                            registry.get(request.name()).invoke(engine, request.arg(0), request.arg(1), callback);
                            break;
                        case 3:
                            registry.get(request.name()).invoke(engine, request.arg(0), request.arg(1), request.arg(2), callback);
                            break;
                        }
                    }
                });
            }
        } catch (RejectedExecutionException e) {
            SimUtils.printError("increase :queue-size if this happens often", e);
        }
    }

    public void close(int timeoutTs) {
        if (timeoutTs > 0) {
            execs.shutdown();
            try {
                if (!execs.awaitTermination(timeoutTs, TimeUnit.MILLISECONDS)) {
                    execs.shutdownNow();
                }
            } catch (InterruptedException ie) {
                execs.shutdownNow();
                Thread.currentThread().interrupt();
            }
        } else {
            execs.shutdownNow();
        }
    }

    public void clientClose(final AsyncChannel channel, final int status) {
        if (channel.closedRan == 0) { // server did not close it first
            // has close handler, execute it in another thread
            if (channel.closeHandler != null) {
                try {
                    // no need to maintain order
                    execs.submit(new Runnable() {
                        public void run() {
                            try {
                                channel.onClose(status);
                            } catch (Exception e) {
                                SimUtils.printError("on close handler", e);
                            }
                        }
                    });
                } catch (RejectedExecutionException e) {
                    SimUtils.printError("increase :queue-size if this happens often", e);
                }
            } else {
                // no close handler, mark the connection as closed
                // channel.closedRan = 1;
                // lazySet
                AsyncChannel.unsafe.putOrderedInt(channel, AsyncChannel.closedRanOffset, 1);
            }
        }
    }

}
