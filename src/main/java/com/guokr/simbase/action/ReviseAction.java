package com.guokr.simbase.action;

import java.nio.ByteBuffer;
import java.util.Map;

import org.wahlque.net.action.Action;
import org.wahlque.net.action.ActionException;
import org.wahlque.net.action.Command;
import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.payload.Bytes;
import org.wahlque.net.transport.payload.Multiple;

import com.guokr.simbase.SimMain;
import com.guokr.simbase.command.Revise;
import com.guokr.simbase.reply.OK;

public class ReviseAction implements Action {

	public static final String ACTION = "vrev";

	public Multiple payload(Map<String, Object> context, Command command)
			throws ActionException {

		Revise cmd = (Revise) command;

		Bytes[] value = new Bytes[cmd.schema.length + 2];

		value[0] = new Bytes(ACTION.getBytes());

		value[1] = new Bytes(cmd.key.getBytes());

		int i = 2;
		for (String dim : cmd.schema) {
			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.put(dim.getBytes());
			value[++i] = new Bytes(bb.array());
		}

		return new Multiple(value);
	}

	public Command command(Map<String, Object> context, Payload<?> payload)
			throws ActionException {

		Revise cmd = new Revise();

		Multiple multiple = (Multiple) payload;
		Payload<?>[] items = multiple.data();

		Bytes actionBytes = (Bytes) items[0];
		assert (new String(actionBytes.data()).equals("add"));

		Bytes keyBytes = (Bytes) items[1];
		cmd.key = new String(keyBytes.data());

		int size = items.length - 2;
		String[] array = new String[size];
		for (int i = 0; i < size; i++) {
			Bytes stringBytes = (Bytes) items[i + 2];
			array[i] = new String(stringBytes.data());
		}
		cmd.schema = array;

		return cmd;
	}

	public Payload<?> apply(Map<String, Object> context, Payload<?> data)
			throws ActionException {
		Revise cmd = (Revise) command(context, data);
		((SimMain) context.get("simbase")).revise(cmd.key, cmd.schema);
		return new OK();
	}

}
