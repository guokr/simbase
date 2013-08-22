package com.guokr.simbase.action;

import java.util.Map;

import org.wahlque.net.action.Action;
import org.wahlque.net.action.ActionException;
import org.wahlque.net.action.Command;
import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.payload.Bytes;
import org.wahlque.net.transport.payload.Multiple;

import com.guokr.simbase.SimBase;
import com.guokr.simbase.command.Schema;
import com.guokr.simbase.reply.Result;

public class SchemaAction implements Action {

	public static final String ACTION = "vsch";

	public Payload<?> payload(Map<String, Object> context, Command command)
			throws ActionException {

		Schema cmd = (Schema) command;

		Bytes[] value = new Bytes[3];

		value[0] = new Bytes(ACTION.getBytes());

		value[1] = new Bytes(cmd.key.getBytes());

		return new Multiple(value);
	}

	public Command command(Map<String, Object> context, Payload<?> payload)
			throws ActionException {

		Schema cmd = new Schema();

		Multiple multiple = (Multiple) payload;
		Payload<?>[] items = multiple.data();

		Bytes actionBytes = (Bytes) items[0];
		assert (new String(actionBytes.data()).equals(ACTION));

		Bytes keyBytes = (Bytes) items[1];
		cmd.key = new String(keyBytes.data());

		return cmd;
	}

	public Payload<?> apply(Map<String, Object> context, Payload<?> data)
			throws ActionException {
		String[] result;
		if (context == null) {
			result = new String[0];
		} else {
			Schema cmd = (Schema) command(context, data);
			result = ((SimBase) context.get("simbase")).schema(cmd.key);
		}
		return new Result(result);
	}
}
