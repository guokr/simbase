package org.wahlque.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wahlque.net.action.ActionRegistry;

public class Server {

	private boolean listening = true;
	private final Map<String, Object> serverContext;
	private final ActionRegistry registry;
	private final ThreadPoolExecutor serverThreadPool = new ThreadPoolExecutor(
			10, 50, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),
			new ServerThreadFactory(), new RejectedHandler());

	
	private static final Logger log = LoggerFactory.getLogger(Server.class);

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
					final Socket socket;
					try {
						socket = serverSocket.accept();
					} catch (IOException e) {
						throw new ServerExcpetion();
					}
					
					if (socket != null && !socket.isClosed()) {
						serverThreadPool.execute(new Runnable() {
							public void run() {
								Session session = registry.initiate(
										new HashMap<String, Object>(
												serverContext), socket);
								((ServerThread) Thread.currentThread())
										.setSeesion(session);
								try {
									while (true) {
										if (!session.isClosed()) {
											session.execute();
										} else {
											break;
										}
									}
								} catch (Throwable e) {
									Throwable cause = e.getCause();
									if (cause != null) {
										log.error("SERVER ERROR!:", cause);
									} else {
										log.error("SERVER ERROR!:", e);
									}

								}
							}
						});
					}
				}
			}

			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServerExcpetion e) {
		}

		log.info("Server shutdown!");
		System.exit(0);
	}

	public class ServerThread extends Thread {
		private Session session = null;

		public ServerThread(Runnable r) {
			super(r);
		}

		public void setSeesion(Session session) {
			this.session = session;
		}

		public void closeSession() {
			this.session.close();
		}
	}

	public class ServerThreadFactory implements ThreadFactory {

		@Override
		public Thread newThread(Runnable r) {
			return new ServerThread(r);
		}

	}

	public class RejectedHandler implements RejectedExecutionHandler {

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		}

	}

}
