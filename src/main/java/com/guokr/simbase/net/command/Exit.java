package com.guokr.simbase.net.command;

import java.util.Map;

import org.wahlque.cmd.Command;
import org.wahlque.net.transport.Payload;

public class Exit implements Command {

	public static final String ACTION = "exit";

	public void from(Map<String, Object> context, Payload<?> data) {
	}

	public Payload<?> to(Map<String, Object> context) {
		return null;
	}

	public Payload<?> apply(Map<String, Object> context, Payload<?> data) {
		return null;
	}

}
