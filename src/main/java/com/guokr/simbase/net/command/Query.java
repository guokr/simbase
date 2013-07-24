package com.guokr.simbase.net.command;

import java.nio.ByteBuffer;

import org.wahlque.net.transport.Command;
import org.wahlque.net.transport.payload.Bytes;

public class Query extends Command {

	private static final String ACTION = "query";

	public Query(int docid) {
		super(ACTION);

		this.value = new Bytes[2];

		this.value[0] = new Bytes("query".getBytes());

		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(docid);
		this.value[1] = new Bytes(bb.array());
	}

}
