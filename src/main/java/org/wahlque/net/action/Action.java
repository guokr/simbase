package org.wahlque.net.action;

import java.util.Map;

import org.wahlque.net.transport.Payload;

public interface Action {

	public Command command(Map<String, Object> context, Payload<?> payload) throws ActionException;

	public Payload<?> payload(Map<String, Object> context, Command command) throws ActionException;

	public Payload<?> apply(Map<String, Object> context, Payload<?> payload) throws ActionException;

}
