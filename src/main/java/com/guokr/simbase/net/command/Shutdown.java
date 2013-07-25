package com.guokr.simbase.net.command;

import java.util.Map;

import org.wahlque.cmd.Command;
import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.payload.Multiple;
import org.wahlque.server.Server;

public class Shutdown implements Command {

	public static final String ACTION = "shutdown";

	public void from(Map<String, Object> context, Payload<?> data) {
	}

	public Payload<?> to(Map<String, Object> context) {
		return null;
	}

	public Payload<?> apply(Map<String, Object> context, Payload<?> data) {
		((Server)context.get("server")).shutdown();
		return null;
	}

	@Override
	public void validate(Payload<?> data) {
	}

}
