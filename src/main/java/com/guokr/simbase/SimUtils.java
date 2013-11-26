package com.guokr.simbase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimUtils {

    public static final Charset                  UTF_8       = Charset.forName("utf8");

    public static final byte                     PLUS        = 43;                     // '+'
    public static final byte                     MINUS       = 45;                     // '-'
    public static final byte                     COLON       = 58;                     // ':'
    public static final byte                     DOLLAR      = 36;                     // '$'
    public static final byte                     STAR        = 42;                     // '*'

    public static final byte                     CR          = 13;                     // \r
    public static final byte                     LF          = 10;                     // \n

    public static final byte                     DOT         = 46;                     // '.'
    public static final byte                     SP          = 32;                     // ' '

    public static final byte[]                   CRLF        = new byte[] { CR, LF };  // '\r\n'
    public static final byte[]                   SPACE       = new byte[] { SP };      // '\r\n'
    public static final byte[]                   OK          = new byte[] { 'O', 'K' };

    public static final int                      ZERO        = '0';

    private static final ThreadLocal<ByteBuffer> localbuffer = new ThreadLocal<ByteBuffer>() {
                                                                 @Override
                                                                 protected ByteBuffer initialValue() {
                                                                     return ByteBuffer.allocate(4);
                                                                 }
                                                             };

    public static void printTrace(String msg) {
        String trace = String.format("%s [%s] TRACE - %s", new Date(), Thread.currentThread().getName(), msg);
        StringWriter str = new StringWriter();
        PrintWriter pw = new PrintWriter(str, false);
        pw.println(trace);
        new Throwable().printStackTrace(pw);
        System.out.print(str.getBuffer().toString());
    }

    public static void printError(String msg, Throwable t) {
        String error = String.format("%s [%s] ERROR - %s", new Date(), Thread.currentThread().getName(), msg);
        StringWriter str = new StringWriter();
        PrintWriter pw = new PrintWriter(str, false);
        pw.println(error);
        t.printStackTrace(pw);
        System.err.print(str.getBuffer().toString());
    }

    public static byte[] size(int length) {
        return bytes(String.valueOf(length));
    }

    public static byte[] size(byte[] bytes) {
        int length = bytes.length;
        return bytes(String.valueOf(length));
    }

    public static byte[] bytes(String msg) {
        return msg.getBytes(UTF_8).clone();
    }

    public static byte[] bytes(int val) {
        ByteBuffer buffer = localbuffer.get();
        buffer.clear();
        buffer.putInt(val);
        return buffer.array().clone();
    }

    public static byte[] bytes(int[] list) {
        int len = list.length;
        byte[] result = new byte[4 * len];
        for (int i = 0; i < len; i++) {
            byte[] array = bytes(list[i]);
            result[4 * i] = array[0];
            result[4 * i + 1] = array[1];
            result[4 * i + 2] = array[2];
            result[4 * i + 3] = array[3];
        }
        return result;
    }

    public static byte[] bytes(float val) {
        ByteBuffer buffer = localbuffer.get();
        buffer.clear();
        buffer.putFloat(val);
        return buffer.array().clone();
    }

    public static byte[] bytes(float[] list) {
        int len = list.length;
        byte[] result = new byte[4 * len];
        for (int i = 0; i < len; i++) {
            byte[] array = bytes(list[i]);
            result[4 * i] = array[0];
            result[4 * i + 1] = array[1];
            result[4 * i + 2] = array[2];
            result[4 * i + 3] = array[3];
        }
        return result;
    }

    public static byte[] bytes(String[] list) {
        int len = list.length;
        List<Byte> blist = new ArrayList<Byte>();
        for (int i = 0; i < len; i++) {
            byte[] array = bytes(list[i]);
            for (byte b : array) {
                blist.add(b);
            }
            //blist.add(0);
        }

        int pos = 0;
        byte[] result = new byte[blist.size()];
        for (byte b : blist) {
            result[pos] = b;
            pos++;
        }
        return result;
    }

}