package com.guokr.simbase;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.guokr.simbase.engine.SimEngine;
import com.guokr.simbase.handler.SimHandler;
import com.guokr.simbase.server.IHandler;
import com.guokr.simbase.server.SimServer;

public class SimBase {

    private SimEngine engine;
    private SimServer server;
    private IHandler  handler;

    public SimBase(SimConfig conf) throws IOException {
        engine = new SimEngine(conf.getSub("engine"));
        handler = new SimHandler(engine);
        server = new SimServer(conf.getSub("server"), handler);
    }

    public void run() throws IOException {
        server.start();
    }

    public static final void main(String[] args) {
        Yaml yaml = new Yaml();
        try {
            @SuppressWarnings("unchecked")
            SimConfig config = new SimConfig((Map<String, Object>)yaml.load(new FileReader("config/simbase.yaml")));
            SimBase database = new SimBase(config);
            database.run();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
