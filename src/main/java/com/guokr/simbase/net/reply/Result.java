package com.guokr.simbase.net.reply;

import java.nio.ByteBuffer;
import java.util.SortedMap;

import org.wahlque.net.transport.payload.Bytes;
import org.wahlque.net.transport.payload.Multiple;

public class Result extends Multiple {

	public Result(SortedMap<Integer, Float> result) {
		int len = 2 * result.size() + 1;

		this.value = new Bytes[len];

		this.value[0] = new Bytes("result".getBytes());

		ByteBuffer bb = null;
		for (int docid : result.keySet()) {
			bb = ByteBuffer.allocate(4);
			bb.putFloat(result.get(docid));
			this.value[--len] = new Bytes(bb.array());

			bb = ByteBuffer.allocate(4);
			bb.putInt(docid);
			this.value[--len] = new Bytes(bb.array());
		}

	}

}
