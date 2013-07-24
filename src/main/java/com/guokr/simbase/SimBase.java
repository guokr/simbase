package com.guokr.simbase;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import org.wahlque.net.transport.Command;
import org.wahlque.net.transport.Transport;
import org.wahlque.net.transport.payload.Multiple;

public class SimBase {

	private boolean listening = true;
	private HashMap<String, SimEngine> base = new HashMap<String, SimEngine>();

	public SimBase(Map<String, Object> config) {
	}

	public synchronized boolean up() {
		return this.listening;
	}

	public synchronized void down() {
		this.listening = false;
		this.notifyAll();
	}

	public void add(String key, int docid, float[] distr) {
		if (!base.containsKey(key)) {
			base.put(key, new SimEngine());
		}
		base.get(key).add(docid, distr);
	}

	public void update(String key, int docid, float[] distr) {
		if (!base.containsKey(key)) {
			base.put(key, new SimEngine());
		}
		base.get(key).update(docid, distr);
	}

	public SortedMap<Integer, Float> retrieve(String key, int docid) {
		return base.get(key).retrieve(docid);
	}

	public static class SimBaseThread extends Thread {
		private SimBase base = null;
		private Socket socket = null;
		private ServerSocket serverSocket;

		public SimBaseThread(SimBase base, ServerSocket serverSocket, Socket socket) {
			super("SimBaseThread");
			this.base = base;
			this.socket = socket;
			this.serverSocket = serverSocket;
		}

		public void run() {
			try {
				Command command = null;
				while (command == null
						|| !(command.action().equals("exit") || command.action().equals("shutdown") )) {
					command = new Command(
							(Multiple) (Transport.readPayload(socket
									.getInputStream())));
					System.out.println(command.toString());
				}
				if (command.action().equals("shutdown")) {
				    this.base.down();
				    this.socket.close();
				    this.serverSocket.close();
				} else {
					this.socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("stoped");
		}
	}

	public static void main(String[] args) throws IOException {

		SimBase db = new SimBase(null);

		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(Integer.parseInt(args[0]));
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + args[0]);
			System.exit(-1);
		}

		try {
		   while (db.up())
			   new SimBaseThread(db, serverSocket, serverSocket.accept()).start();

			serverSocket.close();
		} catch (IOException e) {
			//NOOP
		}
		System.out.println("Server shutdown!");
		System.exit(0);
	}
}
