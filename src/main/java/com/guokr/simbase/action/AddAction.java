package com.guokr.simbase.action;

import java.nio.ByteBuffer;
import java.util.Map;

import org.wahlque.net.action.Action;
import org.wahlque.net.action.ActionException;
import org.wahlque.net.action.Command;
import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.payload.Bytes;
import org.wahlque.net.transport.payload.Multiple;

import com.guokr.simbase.SimBase;
import com.guokr.simbase.command.Add;
import com.guokr.simbase.reply.OK;

public class AddAction implements Action {

	public static final String ACTION = "vadd";

	public Multiple payload(Map<String, Object> context, Command command)
			throws ActionException {

		Add cmd = (Add) command;

		Bytes[] value = new Bytes[cmd.distr.length + 3];

		value[0] = new Bytes("add".getBytes());

		value[1] = new Bytes(cmd.key.getBytes());

		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(cmd.docid);
		value[2] = new Bytes(bb.array());

		int i = 2;
		for (float component : cmd.distr) {
			bb = ByteBuffer.allocate(4);
			bb.putFloat(component);
			value[++i] = new Bytes(bb.array());
		}

		return new Multiple(value);
	}

	public Command command(Map<String, Object> context, Payload<?> payload)
			throws ActionException {

		Add cmd = new Add();

		Multiple multiple = (Multiple) payload;
		Payload<?>[] items = multiple.data();

		Bytes actionBytes = (Bytes) items[0];
		assert (new String(actionBytes.data()).equals("add"));

		Bytes keyBytes = (Bytes) items[1];
		cmd.key = new String(keyBytes.data());

		Bytes docidBytes = (Bytes) items[2];
		cmd.docid = Integer.parseInt(new String(docidBytes.data()));

		int size = items.length - 3;
		float[] array = new float[size];
		for (int i = 0; i < size; i++) {
			Bytes floatBytes = (Bytes) items[i + 3];
			array[i] = Float.parseFloat(new String(floatBytes.data()));
		}
		cmd.distr = array;

		return cmd;
	}

	public Payload<?> apply(Map<String, Object> context, Payload<?> data)
			throws ActionException {
		Add cmd = (Add) command(context, data);
		((SimBase) context.get("simbase")).add(cmd.key, cmd.docid, cmd.distr);
		return new OK();
	}

}
