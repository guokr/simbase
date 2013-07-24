package org.wahlque.net.transport;

import java.nio.ByteBuffer;

import org.wahlque.net.transport.payload.Multiple;

public class Command extends Multiple {

	protected String cmdAction;

	protected Command(String action) {
		this.cmdAction = action;
	}

	public Command(Multiple multiple) {
		if (multiple == null) {
		    this.cmdAction = "exit";
		} else {
		    this.cmdAction = new String((byte[]) (multiple.data()[0].data()));
			this.value = multiple.data();
		}
	}

	public String action() {
		return this.cmdAction;
	}

	public String toString() {
		return "Command[" + this.cmdAction + "]";
	}

}
