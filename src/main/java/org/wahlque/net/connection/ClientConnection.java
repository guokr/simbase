package org.wahlque.net.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.wahlque.net.transport.Command;
import org.wahlque.net.transport.Reply;

/**
 * Implements the Redis Universal Protocol, send/receive a command or a reply
 */
public class ClientConnection {

    private final BufferedInputStream is;
    private final OutputStream os;

    /**
     * Create a new connection from a socket connection.
     */
    public ClientConnection(Socket socket) throws IOException {
        this.is = new BufferedInputStream(socket.getInputStream());
        this.os = new BufferedOutputStream(socket.getOutputStream());
    }

    /**
     * Send a command over the wire, do not wait for a reponse.
     */
    public void send(Command command) throws IOException {
        synchronized (this.os) {
            command.write(os);
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