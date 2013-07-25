package com.guokr.simbase.command;

import org.wahlque.net.action.Command;

public class Put implements Command {

	public String key;
	public int docid;
	public float[] distr;

	@Override
	public String actionName() {
		return "put";
	}

}
