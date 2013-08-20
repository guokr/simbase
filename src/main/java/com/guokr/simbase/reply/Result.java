package com.guokr.simbase.reply;

import gnu.trove.iterator.TFloatIterator;
import gnu.trove.list.TFloatList;

import java.util.Map;
import java.util.SortedSet;

import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.Reply;
import org.wahlque.net.transport.payload.Bytes;
import org.wahlque.net.transport.payload.Multiple;

public class Result extends Multiple implements Reply<Payload<?>[]> {

	public Result(SortedSet<Map.Entry<Integer, Float>> result) {
		super(null);

		int len = 2 * result.size();
		this.value = new Bytes[len];
		int i = 0;
		for (Map.Entry<Integer, Float> entry : result) {
			this.value[i++] = new Bytes(String.valueOf(entry.getKey())
					.getBytes());
			this.value[i++] = new Bytes(String.valueOf(entry.getValue())
					.getBytes());
		}
	}

	public Result(TFloatList result) {
		super(null);

		int len = result.size();
		this.value = new Bytes[len];

		int i = 0;
		TFloatIterator iter = result.iterator();
		while (iter.hasNext()) {
			this.value[i++] = new Bytes(String.valueOf(iter.next())
					.getBytes());
		}
	}
}
