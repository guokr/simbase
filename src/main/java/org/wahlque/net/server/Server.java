package org.wahlque.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.wahlque.net.action.ActionRegistry;
import org.wahlque.net.transport.Transport;
import org.wahlque.net.transport.payload.Multiple;

public class Server {

	private boolean listening = true;
	private final Map<String, Object> serverContext;
	private final ActionRegistry registry;

	public Server(Map<String, Object> context, ActionRegistry registry) {
		this.serverContext = context;
		this.registry = registry;

		this.serverContext.put("server", this);
	}

	public synchronized boolean up() {
		return this.listening;
	}

	public synchronized void down() {
		this.listening = false;
		this.notifyAll();
	}

	public synchronized void shutdown() {
		try {
			((ServerSocket) this.serverContext.get("serverSocket")).close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(int port) {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(port);
			this.serverContext.put("serverSocket", serverSocket);

			while (up()) {
				new ServerThread(serverSocket.accept()).start();
			}

			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Server shutdown!");
		System.exit(0);
	}

	public class ServerThread extends Thread {
		private Socket socket = null;

		public ServerThread(Socket socket) {
			super("ServerThread");
			this.socket = socket;
		}

		public void run() {
			try {
				registry.initiate(new HashMap<String, Object>(serverContext))
						.execute(this.socket);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}
