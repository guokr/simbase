package org.wahlque.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.wahlque.net.action.ActionRegistry;

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
				if (!serverSocket.isClosed()) {
					Socket socket = null;
					try {
						socket = serverSocket.accept();
					} catch (IOException e) {
						throw new ServerExcpetion();
					}
					if (socket != null && !socket.isClosed()) {
						new ServerThread(socket).start();
					}
				}
			}

			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServerExcpetion e) {
		}

		System.out.println("Server shutdown!");
		System.exit(0);
	}

	public class ServerThread extends Thread {
		private Socket socket = null;
		private Session session = null;

		public ServerThread(Socket socket) {
			super("ServerThread");
			this.socket = socket;
			this.session = registry.initiate(new HashMap<String, Object>(
					serverContext), this.socket);
		}

		public void closeSession() {
			this.session.close();
		}

		public void run() {
			try {
				while (true) {
					this.session.execute();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}
