package org.wahlque.net.transport.payload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.Transport;

public class Number implements Payload<Long> {

    public static final char discriminator = ':';
    protected long value;

    public char discriminator() {
        return discriminator;
    }

    public Long data() {
        return value;
    }

    public void read(InputStream is) throws IOException {
        value = Transport.readNumber(is);
    }

    public void write(OutputStream os) throws IOException {
        Transport.writeDiscriminator(os, discriminator);
        Transport.writeNumber(os, value);
    }

}
