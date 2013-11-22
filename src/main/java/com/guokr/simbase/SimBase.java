package com.guokr.simbase;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.guokr.simbase.engine.SimEngineImpl;
import com.guokr.simbase.handler.Ping;
import com.guokr.simbase.server.ServerHandler;
import com.guokr.simbase.server.SimServer;

public class SimBase {

    private SimServer server;

    public SimBase(SimConfig conf) throws IOException {
        SimEngine engine = new SimEngineImpl(conf.getSub("engine"));
        SimRegistry registry = new SimRegistry();
        registry.add("ping", new Ping());

        server = new SimServer(conf.getSub("server"), new ServerHandler(32, "", 100, registry, engine));
    }

    public void run() throws IOException {
        server.start();
    }

    public static final void main(String[] args) {
        Yaml yaml = new Yaml();
        try {
            @SuppressWarnings("unchecked")
            SimConfig config = new SimConfig((Map<String, Object>) yaml.load(new FileReader("config/simbase.yaml")));
            SimBase database = new SimBase(config);
            database.run();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
