package com.guokr.simbase.action;

import java.util.Map;

import org.wahlque.net.action.Action;
import org.wahlque.net.action.ActionException;
import org.wahlque.net.action.Command;
import org.wahlque.net.transport.Payload;

public class ExitAction implements Action {

	public static final String ACTION = "exit";

	public Command command(Map<String, Object> context, Payload<?> payload) throws ActionException {
		return null;
	}

	public Payload<?> payload(Map<String, Object> context, Command command) throws ActionException {
		return null;
	}

	public Payload<?> apply(Map<String, Object> context, Payload<?> data) throws ActionException {
		return null;
	}

}
