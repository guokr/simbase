package org.wahlque.cmd;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.wahlque.net.transport.Transport;
import org.wahlque.net.transport.payload.Multiple;
import org.wahlque.net.transport.payload.Error;
import org.wahlque.server.Session;

public class CommandRegistry {

	private Map<String, Command> registry = new HashMap<String, Command>();
	private Command failback = null;

	public void register(Class<? extends Command> clazz) {
		try {
			Field field = clazz.getDeclaredField("ACTION");
			String action = field.get(null).toString();
			registry.put(action, clazz.newInstance());
		} catch (Exception e) {
			System.out.println("registering failed!");
		}
	}

	public void registerFailback(Class<? extends Command> clazz) {
		try {
			failback = clazz.newInstance();
		} catch (Exception e) {
			System.out.println("registering failed!");
		}
	}

	public Session initiate(final Map<String, Object> context) {
		return new Session() {
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

				Command instance = registry.get(action);
				if (instance == null) {
					instance = failback;
				}
				
				if (instance != null) {
					try {
						instance.validate(multiple);
						Transport.writePayload(clientSocket.getOutputStream(),
								instance.apply(context, multiple));
					} catch (CommandException e) {
						try {
							Transport.writePayload(clientSocket.getOutputStream(), new Error(e.getMessage()));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} catch (IOException e) {
					}
				}
			}
		};
	}

}
