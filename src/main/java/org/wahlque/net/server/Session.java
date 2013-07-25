package org.wahlque.net.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import org.wahlque.net.action.Action;
import org.wahlque.net.action.ActionException;
import org.wahlque.net.action.ActionRegistry;
import org.wahlque.net.transport.Transport;
import org.wahlque.net.transport.payload.Error;
import org.wahlque.net.transport.payload.Multiple;

public class Session {

	private Map<String, Object> context;
	private ActionRegistry registry;

	public Session(ActionRegistry registry, Map<String, Object> context) {
		this.context = context;
		this.registry = registry;
	}

	public void execute(Socket clientSocket) {

		context.put("clientSocket", clientSocket);

		Multiple multiple = null;
		try {
			multiple = (Multiple) (Transport.readPayload(clientSocket
					.getInputStream()));
		} catch (IOException e) {
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

		if (instance != null) {
			try {
				Transport.writePayload(clientSocket.getOutputStream(),
						instance.apply(context, multiple));
			} catch (ActionException e) {
				try {
					Transport.writePayload(clientSocket.getOutputStream(),
							new Error(e.getMessage()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
