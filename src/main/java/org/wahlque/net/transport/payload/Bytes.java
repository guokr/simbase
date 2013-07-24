package org.wahlque.net.transport.payload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.Transport;

public class Bytes implements Payload<byte[]> {

	public static final char discriminator = '$';
	protected byte[] value;

	public Bytes(byte[] value) {
		this.value = value;
	}

	public char discriminator() {
		return discriminator;
	}

	public byte[] data() {
		return value;
	}

	public void read(InputStream is) throws IOException {
		value = Transport.readBytes(is);
	}

	public void write(OutputStream os) throws IOException {
		Transport.writeDiscriminator(os, discriminator);
		Transport.writeBytes(os, value);
	}

}
