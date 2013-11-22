package com.guokr.simbase.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;
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
                        SimCommand command = registry.get(request.name());
                        String sig = command.signature();
                        switch (request.argsize()) {
                        case 0:
                            command.invoke(engine, callback);
                            break;
                        case 1:
                            command.invoke(engine, request.argstring(0), callback);
                            break;
                        case 2:
                            sig = sig.substring(1);
                            if (sig.equals("s")) {
                                command.invoke(engine, request.argstring(0), request.argstring(1), callback);
                            } else if (sig.equals("i")) {
                                command.invoke(engine, request.argstring(0), request.argint(1), callback);
                            } else if (sig.equals("f")) {
                                command.invoke(engine, request.argstring(0), request.argfloat(1), callback);
                            } else if (sig.equals("S")) {
                                command.invoke(engine, request.argstring(0), request.argarraystring(1), callback);
                            } else if (sig.equals("I")) {
                                command.invoke(engine, request.argstring(0), request.argarrayint(1), callback);
                            } else if (sig.equals("F")) {
                                command.invoke(engine, request.argstring(0), request.argarrayfloat(1), callback);
                            }
                            break;
                        case 3:
                            sig = sig.substring(1);
                            if (sig.equals("ss")) {
                                command.invoke(engine, request.argstring(0), request.argstring(1), request.argstring(2), callback);
                            } else if (sig.equals("si")) {
                                command.invoke(engine, request.argstring(0), request.argstring(1), request.argint(2), callback);
                            } else if (sig.equals("sf")) {
                                command.invoke(engine, request.argstring(0), request.argstring(1), request.argfloat(2), callback);
                            } else if (sig.equals("sS")) {
                                command.invoke(engine, request.argstring(0), request.argstring(1), request.argarraystring(2), callback);
                            } else if (sig.equals("sI")) {
                                command.invoke(engine, request.argstring(0), request.argstring(1), request.argarrayint(2), callback);
                            } else if (sig.equals("sF")) {
                                command.invoke(engine, request.argstring(0), request.argstring(1), request.argarrayfloat(2), callback);
                            } else if (sig.equals("is")) {
                                command.invoke(engine, request.argstring(0), request.argint(1), request.argstring(2), callback);
                            } else if (sig.equals("ii")) {
                                command.invoke(engine, request.argstring(0), request.argint(1), request.argint(2), callback);
                            } else if (sig.equals("if")) {
                                command.invoke(engine, request.argstring(0), request.argint(1), request.argfloat(2), callback);
                            } else if (sig.equals("iS")) {
                                command.invoke(engine, request.argstring(0), request.argint(1), request.argarraystring(2), callback);
                            } else if (sig.equals("iI")) {
                                command.invoke(engine, request.argstring(0), request.argint(1), request.argarrayint(2), callback);
                            } else if (sig.equals("iF")) {
                                command.invoke(engine, request.argstring(0), request.argint(1), request.argarrayfloat(2), callback);
                            } else if (sig.equals("fs")) {
                                command.invoke(engine, request.argstring(0), request.argfloat(1), request.argstring(2), callback);
                            } else if (sig.equals("fi")) {
                                command.invoke(engine, request.argstring(0), request.argfloat(1), request.argint(2), callback);
                            } else if (sig.equals("ff")) {
                                command.invoke(engine, request.argstring(0), request.argfloat(1), request.argfloat(2), callback);
                            } else if (sig.equals("fS")) {
                                command.invoke(engine, request.argstring(0), request.argfloat(1), request.argarraystring(2), callback);
                            } else if (sig.equals("fI")) {
                                command.invoke(engine, request.argstring(0), request.argfloat(1), request.argarrayint(2), callback);
                            }
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
