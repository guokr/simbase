package com.guokr.simbase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import org.wahlque.net.action.ActionRegistry;
import org.wahlque.net.server.Server;

import com.guokr.simbase.action.AddAction;
import com.guokr.simbase.action.ExitAction;
import com.guokr.simbase.action.PutAction;
import com.guokr.simbase.action.QueryAction;
import com.guokr.simbase.action.ShutdownAction;

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

		ActionRegistry registry = ActionRegistry.getInstance();
		registry.register(AddAction.class);
		registry.register(PutAction.class);
		registry.register(QueryAction.class);
		registry.register(ExitAction.class);
		registry.register(ShutdownAction.class);

		Server server = new Server(context, registry);
		server.run(7654);
	}
}
