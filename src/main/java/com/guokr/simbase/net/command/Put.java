package com.guokr.simbase.net.command;

import java.nio.ByteBuffer;
import java.util.Map;

import org.wahlque.cmd.Command;
import org.wahlque.cmd.CommandException;
import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.payload.Bytes;
import org.wahlque.net.transport.payload.Multiple;

import com.guokr.simbase.SimBase;
import com.guokr.simbase.net.reply.OK;

public class Put implements Command {

	public static final String ACTION = "put";

	private String key;
	private int docid;
	private float[] distr;

	public void data(String key, int docid, float... distr) {
		this.key = key;
		this.docid = docid;
		this.distr = distr;
	}

	public Multiple to(Map<String, Object> context) {
		boolean debug = false;
		if (context.containsKey("debug")) {
			debug = ((Boolean) (context.get("debug"))).booleanValue();
		}

		Bytes[] value = new Bytes[distr.length + 3];

		value[0] = new Bytes("add".getBytes());

		value[1] = new Bytes(this.key.getBytes());

		if (!debug) {
			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.putInt(docid);
			value[2] = new Bytes(bb.array());

			int i = 2;
			for (float component : distr) {
				bb = ByteBuffer.allocate(4);
				bb.putFloat(component);
				value[++i] = new Bytes(bb.array());
			}
		} else {
			value[2] = new Bytes(String.valueOf(docid).getBytes());

			int i = 2;
			for (float component : distr) {
				value[++i] = new Bytes(String.valueOf(component).getBytes());
			}
		}

		return new Multiple(value);
	}

	public void from(Map<String, Object> context, Payload<?> data) {
		boolean debug = false;
		if (context.containsKey("debug")) {
			debug = ((Boolean) (context.get("debug"))).booleanValue();
		}
	}

	public Payload<?> apply(Map<String, Object> context, Payload<?> data) {
		this.from(context, data);
		((SimBase) context.get("simbase"))
				.update(this.key, this.docid, this.distr);
		return new OK();
	}

	@Override
	public void validate(Payload<?> data) throws CommandException {
	}

}
