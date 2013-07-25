package org.wahlque.net.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import org.wahlque.net.action.Action;
import org.wahlque.net.action.ActionException;
import org.wahlque.net.action.ActionRegistry;
import org.wahlque.net.action.Command;
import org.wahlque.net.transport.Reply;

/**
 * Implements the Redis Universal Protocol, send/receive a command or a reply
 */
public class ClientConnection {

    private final BufferedInputStream is;
    private final OutputStream os;
	private Map<String, Object> context;

    /**
     * Create a new connection from a socket connection.
     */
    public ClientConnection(Map<String, Object> context, Socket socket) throws IOException {
        this.is = new BufferedInputStream(socket.getInputStream());
        this.os = new BufferedOutputStream(socket.getOutputStream());
        this.context = context;
    }

    /**
     * Send a command over the wire, do not wait for a reponse.
     */
    public void send(Command command) throws IOException {
        synchronized (this.os) {
        	String name = command.actionName();
        	Action action = ActionRegistry.getInstance().get(name);
            try {
				action.payload(this.context, command).write(os);
			} catch (ActionException e) {
				e.printStackTrace();
			}
        }
        this.os.flush();
    }

    /**
     * Wait for a reply on the input stream.
     */
    public Reply<?> receive() throws IOException {
        synchronized (this.is) {
            return null;
        }
    }

    /**
     * Close the input and output streams. Will also disconnect the socket.
     */
    public void close() throws IOException {
        this.is.close();
        this.os.close();
    }
}