package org.wahlque.net.action;

import java.util.Map;

import org.wahlque.net.transport.Payload;

public class UnknownCommandAction implements Action {

	@Override
	public Command command(Map<String, Object> context, Payload<?> payload)
			throws ActionException {
		throw new ActionException("Unknown command from client!");
	}

	@Override
	public Payload<?> payload(Map<String, Object> context, Command command)
			throws ActionException {
		return null;
	}

	@Override
	public Payload<?> apply(Map<String, Object> context, Payload<?> payload)
			throws ActionException {
		throw new ActionException("Unknown command from client!");
	}

}
