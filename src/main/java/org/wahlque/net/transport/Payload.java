package org.wahlque.net.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Payload<T> {

    public char discriminator();

    public T data();

    public void read(InputStream is) throws IOException;

    public void write(OutputStream os) throws IOException;

}
