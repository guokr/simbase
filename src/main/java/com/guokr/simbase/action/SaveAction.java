package com.guokr.simbase.action;

import java.util.Map;

import org.wahlque.net.action.Action;
import org.wahlque.net.action.ActionException;
import org.wahlque.net.action.Command;
import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.payload.Bytes;
import org.wahlque.net.transport.payload.Multiple;

import com.guokr.simbase.SimBase;
import com.guokr.simbase.command.Save;
import com.guokr.simbase.reply.OK;

public class SaveAction implements Action {

	public static final String ACTION = "save";

	public Multiple payload(Map<String, Object> context, Command command)
			throws ActionException {

		Save cmd = (Save) command;

		Bytes[] value = new Bytes[3];

		value[0] = new Bytes(ACTION.getBytes());

		value[1] = new Bytes(cmd.key.getBytes());

		return new Multiple(value);
	}

	public Command command(Map<String, Object> context, Payload<?> payload)
			throws ActionException {
		Save cmd = new Save();

		Multiple multiple = (Multiple) payload;
		Payload<?>[] items = multiple.data();

		Bytes actionBytes = (Bytes) items[0];
		assert (new String(actionBytes.data()).equals(ACTION));

		Bytes keyBytes = (Bytes) items[1];
		cmd.key = new String(keyBytes.data());

		return cmd;
	}

	public Payload<?> apply(Map<String, Object> context, Payload<?> data)
			throws ActionException {
		Save cmd = (Save) command(context, data);
		SimBase test = ((SimBase) context.get("simbase"));
	    if(cmd.key.equals("all")){
	    	test.save();
		}
	    else{
	    	test.save(cmd.key);
	    }
		return new OK();
	}

}
