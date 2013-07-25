package org.wahlque.net.transport;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.wahlque.net.transport.payload.Bytes;
import org.wahlque.net.transport.payload.Error;
import org.wahlque.net.transport.payload.Multiple;
import org.wahlque.net.transport.payload.Number;
import org.wahlque.net.transport.payload.Status;

public class Transport {

	public static final char CR = '\r';
	public static final char LF = '\n';
	public static final byte[] CRLF = new byte[] { CR, LF };

	private static final char ZERO = '0';

	public static void writeDiscriminator(OutputStream os, char discriminator) {
	}

	/**
	 * Read text for a signed integer from the input stream.
	 */
	public static long readNumber(InputStream is) throws IOException {
		int sign = 1, next = is.read();
		long number = 0;

		if (next == '-') {
			next = is.read();
			sign = -1;
		}

		while (true) {
			if (next == -1) {
				throw new EOFException("Unexpected end");
			} else if (next == CR) {
				if (is.read() == LF) {
					number = number * sign;
					break;
				}
			}

			int digit = next - ZERO;
			if (digit >= 0 && digit < 10) {
				number = number * 10 + digit;
			} else {
				throw new IOException(
						"Invalid character in the section for size");
			}
			next = is.read();
		}

		return number;
	}

	/**
	 * Write a signed integer to the output stream.
	 */
	public static void writeNumber(OutputStream os, long value) {
	}

	/**
	 * Read text for a signed integer from the input stream.
	 */
	public static int readSize(InputStream is) throws IOException {
		long size = readNumber(is);
		if (size < 0) {
			throw new IllegalArgumentException(
					"Server only supports nonnegitive arrays size");
		}
		if (size > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Server only supports arrays up to " + Integer.MAX_VALUE
							+ " in size");
		}
		return (int) size;
	}

	public static void writeSize(OutputStream os, int length) {
	}

	/**
	 * Read bytes by size from the stream.
	 */
	public static byte[] readBytes(InputStream is) throws IOException {
		byte[] bytes = null;

		int size = readSize(is);
		if (size < -1) {
			throw new IllegalArgumentException("Invalid size: " + size);
		} else if (size > -1) {
			int total = 0, count = 0;
			bytes = new byte[(int) size];
			while (total < bytes.length
					&& (count = is.read(bytes, total, bytes.length - total)) != -1) {
				total += count;
			}
			if (total < bytes.length) {
				throw new IOException("No sufficient bytes to read: " + total);
			}
			int cr = is.read(), lf = is.read();
			if (cr != CR || lf != LF) {
				throw new IOException(
						"Invalid ending character in the section for bytes: "
								+ cr + ", " + lf);
			}
		}

		return bytes;
	}

	public static void writeBytes(OutputStream os, byte[] value) {
	}

	public static String readString(InputStream is) {
		return null;
	}

	public static void writeString(OutputStream os, String value) {
	}

	/**
	 * Read a Reply from an input stream.
	 */
	public static Payload<?> readPayload(InputStream is) throws IOException {
		Payload<?> payload = null;

		int discr = is.read();
		System.out.print((char) discr);

		switch (discr) {
		case -1:
			payload = null;
			break;
		case Status.discriminator:
			payload = new Status(null);
			break;
		case Error.discriminator:
			payload = new Error();
			break;
		case Number.discriminator:
			payload = new Number();
			break;
		case Bytes.discriminator:
			payload = new Bytes(null);
			break;
		case Multiple.discriminator:
			payload = new Multiple(null);
			break;
		default:
			throw new IOException(String.valueOf((char) discr));
		}

		if (payload != null) {
			payload.read(is);
		}

		return payload;
	}

	/**
	 * Write a Reply to an output stream.
	 */
	public static void writePayload(OutputStream os, Payload<?> payload)
			throws IOException {
		if (payload != null) {
			payload.write(os);
		}
	}

}
