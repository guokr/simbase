package org.wahlque.net.transport.payload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.Transport;

public class Multiple implements Payload<Payload<?>[]> {

    public static final char discriminator = '*';
    protected Payload<?>[] value;

    public Multiple(Payload<?>[] data) {
    	this.value = data;
    }

    public char discriminator() {
        return discriminator;
    }

    public Payload<?>[] data() {
        return value;
    }

    public void read(InputStream is) throws IOException {
    	int size = Transport.readSize(is);
        value = new Payload<?>[size];
        for (int i = 0; i < size; i++) {
        	value[i] = Transport.readPayload(is);
        }
    }

    public void write(OutputStream os) throws IOException {
        Transport.writeDiscriminator(os, discriminator);
        Transport.writeSize(os, value.length);
        Transport.writeCRLF(os);

        for (int i = 0; i < value.length; i++) {
        	System.out.print(new String((byte[]) value[i].data()));
        	value[i].write(os);
        	Transport.writeCRLF(os);
        }
    }

}
