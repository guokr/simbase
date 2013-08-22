package com.guokr.simbase.action;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

import org.wahlque.net.action.Action;
import org.wahlque.net.action.ActionException;
import org.wahlque.net.action.Command;
import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.payload.Bytes;
import org.wahlque.net.transport.payload.Multiple;

import com.guokr.simbase.SimBase;
import com.guokr.simbase.SimTable;
import com.guokr.simbase.command.Get;
import com.guokr.simbase.reply.Result;

public class RetrieveAction implements Action {

	public static final String ACTION = "vretr";

	public Payload<?> payload(Map<String, Object> context, Command command)
			throws ActionException {

		Get cmd = (Get) command;

		Bytes[] value = new Bytes[3];

		value[0] = new Bytes(ACTION.getBytes());

		value[1] = new Bytes(cmd.key.getBytes());

		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(cmd.docid);
		value[2] = new Bytes(bb.array());

		return new Multiple(value);
	}

	public Command command(Map<String, Object> context, Payload<?> payload)
			throws ActionException {

		Get cmd = new Get();

		Multiple multiple = (Multiple) payload;
		Payload<?>[] items = multiple.data();

		Bytes actionBytes = (Bytes) items[0];
		assert (new String(actionBytes.data()).equals(ACTION));

		Bytes keyBytes = (Bytes) items[1];
		cmd.key = new String(keyBytes.data());

		Bytes docidBytes = (Bytes) items[2];
		cmd.docid = Integer.parseInt(new String(docidBytes.data()));

		return cmd;
	}

	public Payload<?> apply(Map<String, Object> context, Payload<?> data)
			throws ActionException {
		SortedSet<Map.Entry<Integer, Float>> result;
		if (context == null) {
			result = SimTable
					.entriesSortedByValues(new TreeMap<Integer, Float>());
		} else {
			Get cmd = (Get) command(context, data);
			result = ((SimBase) context.get("simbase")).retrieve(cmd.key,
					cmd.docid);
		}
		return new Result(result);
	}
}
