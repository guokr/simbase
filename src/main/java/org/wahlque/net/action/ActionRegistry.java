package org.wahlque.net.action;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.wahlque.net.server.Session;

public class ActionRegistry {
	
    private static final ActionRegistry instance = new ActionRegistry();
    
    private ActionRegistry() {}
 
    public static ActionRegistry getInstance() {
        return instance;
    }

	private Map<String, Action> registry = new HashMap<String, Action>();
	private Action failback = new UnknownCommandAction();

	public void register(Class<? extends Action> clazz) {
		try {
			Field field = clazz.getDeclaredField("ACTION");
			String action = field.get(null).toString();
			registry.put(action, clazz.newInstance());
		} catch (Exception e) {
			System.out.println("registering failed!");
		}
	}

	public void registerFailback(Class<? extends Action> clazz) {
		try {
			failback = clazz.newInstance();
		} catch (Exception e) {
			System.out.println("registering failed!");
		}
	}

	public Session initiate(final Map<String, Object> context) {
		return new Session(this, context);
	}

	public Action get(String action) {
		return this.registry.get(action);
	}

	public Action failback() {
		return this.failback;
	}

}
