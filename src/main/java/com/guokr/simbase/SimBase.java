package com.guokr.simbase;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.guokr.simbase.command.BGet;
import com.guokr.simbase.command.BList;
import com.guokr.simbase.command.BLoad;
import com.guokr.simbase.command.BMk;
import com.guokr.simbase.command.BRev;
import com.guokr.simbase.command.BSave;
import com.guokr.simbase.command.Del;
import com.guokr.simbase.command.IAcc;
import com.guokr.simbase.command.IAdd;
import com.guokr.simbase.command.IGet;
import com.guokr.simbase.command.ISet;
import com.guokr.simbase.command.Load;
import com.guokr.simbase.command.Ping;
import com.guokr.simbase.command.RGet;
import com.guokr.simbase.command.RList;
import com.guokr.simbase.command.RMk;
import com.guokr.simbase.command.RRec;
import com.guokr.simbase.command.Save;
import com.guokr.simbase.command.VAcc;
import com.guokr.simbase.command.VAdd;
import com.guokr.simbase.command.VGet;
import com.guokr.simbase.command.VIds;
import com.guokr.simbase.command.VLen;
import com.guokr.simbase.command.VList;
import com.guokr.simbase.command.VMk;
import com.guokr.simbase.command.VRem;
import com.guokr.simbase.command.VSet;
import com.guokr.simbase.command.XAcc;
import com.guokr.simbase.command.XPrd;
import com.guokr.simbase.engine.SimEngineImpl;
import com.guokr.simbase.server.ServerHandler;
import com.guokr.simbase.server.SimServer;

public class SimBase {

    private SimServer server;

    public SimBase(SimConfig conf) throws IOException {
        SimEngine engine = new SimEngineImpl(conf.getSub("engine", "engine"));
        SimRegistry registry = new SimRegistry();

        registry.add("ping", new Ping());
        registry.add("save", new Save());
        registry.add("load", new Load());
        registry.add("del", new Del());

        registry.add("bsave", new BSave());
        registry.add("bload", new BLoad());
        registry.add("blist", new BList());
        registry.add("bmk", new BMk());
        registry.add("bget", new BGet());
        registry.add("brev", new BRev());

        registry.add("vlist", new VList());
        registry.add("vmk", new VMk());
        registry.add("vlen", new VLen());
        registry.add("vids", new VIds());
        registry.add("vadd", new VAdd());
        registry.add("vset", new VSet());
        registry.add("vget", new VGet());
        registry.add("vacc", new VAcc());
        registry.add("vrem", new VRem());
        registry.add("iadd", new IAdd());
        registry.add("iget", new IGet());
        registry.add("iset", new ISet());
        registry.add("iacc", new IAcc());

        registry.add("rlist", new RList());
        registry.add("rmk", new RMk());
        registry.add("rget", new RGet());
        registry.add("rrec", new RRec());

        registry.add("xacc", new XAcc());
        registry.add("xprd", new XPrd());

        server = new SimServer(conf.getSub("server", "server"), new ServerHandler(32, "simbase", 100, registry, engine));

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
