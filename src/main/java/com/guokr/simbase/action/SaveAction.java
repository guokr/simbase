package com.guokr.simbase.action;

import java.util.Map;

import org.wahlque.net.action.Action;
import org.wahlque.net.action.ActionException;
import org.wahlque.net.action.Command;
import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.payload.Multiple;

import com.guokr.simbase.SimBase;
import com.guokr.simbase.command.Save;
import com.guokr.simbase.reply.OK;

public class SaveAction implements Action {

	public static final String ACTION = "save";

	public Multiple payload(Map<String, Object> context, Command command)
			throws ActionException {
		return null;
	}

	public Command command(Map<String, Object> context, Payload<?> payload)
			throws ActionException {
		return null;
	}

	public Payload<?> apply(Map<String, Object> context, Payload<?> data)
			throws ActionException {
		Save cmd = (Save) command(context, data);
		((SimBase) context.get("simbase")).save(cmd.key);
		return new OK();
	}

}
