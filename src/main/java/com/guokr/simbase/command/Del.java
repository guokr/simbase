package com.guokr.simbase.command;

import org.wahlque.net.action.Command;

public class Del implements Command {

	public String key;
	public int docid;
	
	@Override
	public String actionName() {
		return "del";
	}

}
