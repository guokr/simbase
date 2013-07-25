package org.wahlque.server;

import java.net.Socket;

import org.wahlque.net.transport.Reply;

public interface Session {

	public void execute(Socket clientSocket);

}
