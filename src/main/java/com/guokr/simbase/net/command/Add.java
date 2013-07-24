package com.guokr.simbase.net.command;

import java.nio.ByteBuffer;

import org.wahlque.net.transport.Command;
import org.wahlque.net.transport.payload.Bytes;

public class Add extends Command {

	private static final String ACTION = "add";

	public Add(int docid, float... distr) {
		super(ACTION);

		this.value = new Bytes[distr.length + 2];

		this.value[0] = new Bytes("add".getBytes());

		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(docid);
		this.value[1] = new Bytes(bb.array());

		int i = 1;
		for (float component : distr) {
			bb = ByteBuffer.allocate(4);
			bb.putFloat(component);
			this.value[++i] = new Bytes(bb.array());
		}

	}

}
