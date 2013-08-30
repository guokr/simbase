package org.wahlque.net.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wahlque.net.action.Action;
import org.wahlque.net.action.ActionRegistry;
import org.wahlque.net.transport.Transport;
import org.wahlque.net.transport.payload.Error;
import org.wahlque.net.transport.payload.Multiple;

public class Session {

	private static final Logger logger = LoggerFactory.getLogger(Session.class);

	private Map<String, Object> context;
	private ActionRegistry registry;

	public Session(ActionRegistry registry, Map<String, Object> context,
			Socket clientSocket) {
		this.context = context;
		this.registry = registry;
		try {
			if (!context.containsKey("clientSocket")) {
				context.put("clientSocket", clientSocket);
			}
			if (!context.containsKey("inputStream")) {
				context.put("inputStream", clientSocket.getInputStream());
			}
			if (!context.containsKey("outputStream")) {
				context.put("outputStream", clientSocket.getOutputStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("session construct error", e);
		}
	}

	public void execute() {

		InputStream ins = (InputStream) this.context.get("inputStream");
		OutputStream outs = (OutputStream) this.context.get("outputStream");

		Multiple multiple = null;
		try {
			multiple = (Multiple) (Transport.readPayload(ins));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("command reading error", e);
		}

		String action = "exit";
		if (multiple != null) {
			action = new String((byte[]) (multiple.data()[0].data()))
					.toLowerCase();
		}

		Action instance = registry.get(action);
		if (instance == null) {
			instance = registry.failback();
		}

		Exception err = null;
		try {
			Transport.writePayload(outs, instance.apply(context, multiple));
			outs.flush();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("payload writing error", e);
			err = e;
		}

		if (err != null) {
			try {
				Transport.writePayload(outs, new Error(err.getMessage()));
				outs.flush();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("error payload writing error", e);
			}
		}

	}

	public boolean isClosed() {
		Socket clientSocket = (Socket) context.get("clientSocket");
		if (clientSocket == null) {
			return true;
		} else {
			return clientSocket.isClosed();
		}
	}

	public void close() {
		try {
			Socket clientSocket = (Socket) context.remove("clientSocket");
			InputStream ins = (InputStream) this.context.remove("inputStream");
			OutputStream outs = (OutputStream) this.context
					.remove("outputStream");
			if (ins != null) {
				ins.close();
			}
			if (outs != null) {
				outs.flush();
				outs.close();
			}
			clientSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("session closing error", e);
		}
	}

}
