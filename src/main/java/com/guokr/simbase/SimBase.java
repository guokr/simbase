package com.guokr.simbase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import org.wahlque.cmd.CommandRegistry;
import org.wahlque.server.Server;

import com.guokr.simbase.net.command.Add;
import com.guokr.simbase.net.command.Exit;
import com.guokr.simbase.net.command.Put;
import com.guokr.simbase.net.command.Query;
import com.guokr.simbase.net.command.Shutdown;

public class SimBase {

	private Map<String, SimEngine> base = new HashMap<String, SimEngine>();

	public SimBase() {
	}

	public void add(String key, int docid, float[] distr) {
		if (!base.containsKey(key)) {
			base.put(key, new SimEngine());
		}
		base.get(key).add(docid, distr);
	}

	public void update(String key, int docid, float[] distr) {
		if (!base.containsKey(key)) {
			base.put(key, new SimEngine());
		}
		base.get(key).update(docid, distr);
	}

	public SortedMap<Integer, Float> retrieve(String key, int docid) {
		return base.get(key).retrieve(docid);
	}

	public static void main(String[] args) throws IOException {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("debug", true);
		
		SimBase db = new SimBase();
		context.put("simbase", db);

		CommandRegistry registry = new CommandRegistry();
		registry.register(Add.class);
		registry.register(Put.class);
		registry.register(Query.class);
		registry.register(Exit.class);
		registry.register(Shutdown.class);

		Server server = new Server(context, registry);
		server.run(7654);
	}
}
