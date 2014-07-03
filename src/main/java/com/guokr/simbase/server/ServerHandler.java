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
import com.guokr.simbase.util.PrefixThreadFactory;

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

    public void handle(final RedisRequests reqs, final RespCallback cb) {
        execs.submit(new Runnable() {
            @Override
            public void run() {
                int order = 0;
                ServerCallback root = new ServerCallback(reqs.length(), cb);
                for (final SimRequest request : reqs) {
                    SimCallback callback = new PipedCallback(order++, root);
                    try {
                        SimCommand command = registry.get(request.name());
                        String sig = command.signature();
                        switch (sig.length()) {
                        case 0:
                            command.invoke(engine, callback);
                            break;
                        case 1:
                            command.invoke(engine, request.args(1), callback);
                            break;
                        case 2:
                            sig = sig.substring(1);
                            if (sig.equals("s")) {
                                command.invoke(engine, request.args(1), request.args(2), callback);
                            } else if (sig.equals("l")) {
                                command.invoke(engine, request.args(1), request.argl(2), callback);
                            } else if (sig.equals("f")) {
                                command.invoke(engine, request.args(1), request.argf(2), callback);
                            } else if (sig.equals("S")) {
                                command.invoke(engine, request.args(1), request.argS(2), callback);
                            } else if (sig.equals("I")) {
                                command.invoke(engine, request.args(1), request.argl(2), callback);
                            } else if (sig.equals("F")) {
                                command.invoke(engine, request.args(1), request.argF(2), callback);
                            }
                            break;
                        case 3:
                            sig = sig.substring(1);
                            if (sig.equals("ss")) {
                                command.invoke(engine, request.args(1), request.args(2), request.args(3), callback);
                            } else if (sig.equals("sl")) {
                                command.invoke(engine, request.args(1), request.args(2), request.argl(3), callback);
                            } else if (sig.equals("sf")) {
                                command.invoke(engine, request.args(1), request.args(2), request.argf(3), callback);
                            } else if (sig.equals("sS")) {
                                command.invoke(engine, request.args(1), request.args(2), request.argS(3), callback);
                            } else if (sig.equals("sI")) {
                                command.invoke(engine, request.args(1), request.args(2), request.argl(3), callback);
                            } else if (sig.equals("sF")) {
                                command.invoke(engine, request.args(1), request.args(2), request.argF(3), callback);
                            } else if (sig.equals("ls")) {
                                command.invoke(engine, request.args(1), request.argl(2), request.args(3), callback);
                            } else if (sig.equals("ll")) {
                                command.invoke(engine, request.args(1), request.argl(2), request.argl(3), callback);
                            } else if (sig.equals("lf")) {
                                command.invoke(engine, request.args(1), request.argl(2), request.argf(3), callback);
                            } else if (sig.equals("lS")) {
                                command.invoke(engine, request.args(1), request.argl(2), request.argS(3), callback);
                            } else if (sig.equals("lI")) {
                                command.invoke(engine, request.args(1), request.argl(2), request.argl(3), callback);
                            } else if (sig.equals("lF")) {
                                command.invoke(engine, request.args(1), request.argl(2), request.argF(3), callback);
                            } else if (sig.equals("fs")) {
                                command.invoke(engine, request.args(1), request.argf(2), request.args(3), callback);
                            } else if (sig.equals("fl")) {
                                command.invoke(engine, request.args(1), request.argf(2), request.argl(3), callback);
                            } else if (sig.equals("ff")) {
                                command.invoke(engine, request.args(1), request.argf(2), request.argf(3), callback);
                            } else if (sig.equals("fS")) {
                                command.invoke(engine, request.args(1), request.argf(2), request.argS(3), callback);
                            } else if (sig.equals("fI")) {
                                command.invoke(engine, request.args(1), request.argf(2), request.argl(3), callback);
                            }
                            break;
                        case 4:
                            sig = sig.substring(1);
                            if (sig.equals("lsl")) {
                                command.invoke(engine, request.args(1), request.argl(2), request.args(3),
                                        request.argl(4), callback);
                            } else if (sig.equals("lsL")) {
                                command.invoke(engine, request.args(1), request.argl(2), request.args(3),
                                        request.argL(4), callback);
                            }
                            break;
                        }
                    } catch (Exception e) {
                        if (e instanceof IndexOutOfBoundsException) {
                            callback.error(String.format("Invalid arguments for command '%s'", request.name()));
                        } else {
                            callback.error(e.getMessage());
                        }
                        callback.response();
                    }
                }
            }
        });
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
