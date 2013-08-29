package com.guokr.simbase.reply;

import gnu.trove.iterator.TFloatIterator;
import gnu.trove.list.TFloatList;

import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.Reply;
import org.wahlque.net.transport.payload.Bytes;
import org.wahlque.net.transport.payload.Multiple;

public class Result extends Multiple implements Reply<Payload<?>[]> {

	public Result(TFloatList result) {
		super(null);

		int len = result.size();
		this.value = new Bytes[len];

		int i = 0;
		TFloatIterator iter = result.iterator();
		while (iter.hasNext()) {
			this.value[i++] = new Bytes(String.valueOf(iter.next()).getBytes());
		}
	}

	public Result(String[] result) {
		super(null);

		int len = result.length;
		this.value = new Bytes[len];

		int i = 0;
		for (String dim : result) {
			this.value[i++] = new Bytes(String.valueOf(dim).getBytes());
		}
	}
}
