package com.guokr.simbase;

import java.io.IOException;

import com.guokr.simbase.engine.SimEngine;
import com.guokr.simbase.handler.SimHandler;
import com.guokr.simbase.server.IHandler;
import com.guokr.simbase.server.SimServer;

public class SimBase {

    private SimEngine engine;
    private SimServer server;
    private IHandler handler;

    public SimBase(SimConfig conf) throws IOException {
        engine = new SimEngine(conf.getSub("engine"));
        handler = new SimHandler(engine);
        server = new SimServer(conf.getSub("server"), handler);
        server.start();
    }

}
