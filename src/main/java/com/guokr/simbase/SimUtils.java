package com.guokr.simbase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Date;

public class SimUtils {

    public static final Charset UTF_8  = Charset.forName("utf8");

    public static final byte    PLUS   = 43;                     // '+'
    public static final byte    MINUS  = 45;                     // '-'
    public static final byte    COLON  = 58;                     // ':'
    public static final byte    DOLLAR = 36;                     // '$'
    public static final byte    STAR   = 42;                     // '*'

    public static final byte    CR     = 13;                     // \r
    public static final byte    LF     = 10;                     // \n

    public static final byte    DOT    = 46;                     // '.'
    public static final byte    SP     = 32;                     // ' '

    public static final byte[]  CRLF   = new byte[] { CR, LF };  // '\r\n'
    public static final byte[]  SPACE  = new byte[] { SP };      // '\r\n'
    public static final byte[]  OK     = new byte[] { 'O', 'K' };
    public static final byte[]  NIL    = new byte[] { '-', '1' };

    public static final int     ZERO   = '0';

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
        return msg.getBytes(UTF_8);
    }

    public static byte[] bytes(int val) {
        return String.format("%d", val).getBytes(UTF_8);
    }

    public static byte[] bytes(long val) {
        return String.format("%d", val).getBytes(UTF_8);
    }

    public static byte[] bytes(float val) {
        if (val == (int) val)
            return String.format("%d", (int) val).getBytes(UTF_8);
        else
            return String.format("%s", val).getBytes(UTF_8);
    }

}