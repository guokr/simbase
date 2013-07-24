package com.guokr.simbase.net.command;

import org.wahlque.net.transport.Command;

public class Shutdown extends Command {

	private static final String ACTION = "shutdown";

	public Shutdown() {
		super(ACTION);
	}

}
