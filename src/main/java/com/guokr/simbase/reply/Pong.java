package com.guokr.simbase.reply;

import org.wahlque.net.transport.Reply;
import org.wahlque.net.transport.payload.Status;

public class Pong extends Status implements Reply<String> {

	public Pong() {
		super("PONG");
	}

}
