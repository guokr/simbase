package com.guokr.simbase.command;

import org.wahlque.net.action.Command;

public class Update implements Command {

	public String key;
	public int docid;
	public Object[] pairs;

	@Override
	public String actionName() {
		return "vupdt";
	}

}
