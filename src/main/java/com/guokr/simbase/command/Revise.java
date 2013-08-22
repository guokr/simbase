package com.guokr.simbase.command;

import org.wahlque.net.action.Command;

public class Revise implements Command {

	public String key;
	public String[] schema;

	@Override
	public String actionName() {
		return "vrev";
	}

}
