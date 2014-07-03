package com.guokr.simbase;

import java.nio.ByteBuffer;

public abstract class SimCallback {

    public ByteBuffer buffer;

    public void flip() {
        if (buffer != null) {
            buffer.flip();
        }
    }

    public void ok() {
        buffer = ByteBuffer.allocate(5);
        buffer.put(SimUtils.PLUS);
        buffer.put(SimUtils.OK);
        buffer.put(SimUtils.CRLF);
    }

    public void ok(String msg) {
        byte[] bytes = SimUtils.bytes(msg);
        buffer = ByteBuffer.allocate(bytes.length + 3);
        buffer.put(SimUtils.PLUS);
        buffer.put(bytes);
        buffer.put(SimUtils.CRLF);
    }

    public void nil() {
        buffer = ByteBuffer.allocate(5);
        buffer.put(SimUtils.DOLLAR);
        buffer.put(SimUtils.NIL);
        buffer.put(SimUtils.CRLF);
    }

    public void error(String msg) {
        byte[] bytes = SimUtils.bytes(msg);
        buffer = ByteBuffer.allocate(bytes.length + 3);
        buffer.put(SimUtils.MINUS);
        buffer.put(bytes);
        buffer.put(SimUtils.CRLF);
    }

    public void status(int code) {
        byte[] bytes = SimUtils.bytes(code);
        buffer = ByteBuffer.allocate(bytes.length + 3);
        buffer.put(SimUtils.COLON);
        buffer.put(bytes);
        buffer.put(SimUtils.CRLF);
    }

    public void integerValue(int val) {
        byte[] bytes = SimUtils.bytes(val);
        byte[] size = SimUtils.size(bytes);
        buffer = ByteBuffer.allocate(bytes.length + size.length + 5);
        buffer.put(SimUtils.DOLLAR);
        buffer.put(size);
        buffer.put(SimUtils.CRLF);
        buffer.put(bytes);
        buffer.put(SimUtils.CRLF);
    }

    public void integerList(int[] list) {
        byte[] size = SimUtils.size(list.length);
        buffer = ByteBuffer.allocate(1024 * list.length + 1024);
        buffer.put(SimUtils.STAR);
        buffer.put(size);
        buffer.put(SimUtils.CRLF);
        for (int val : list) {
            byte[] bytes = SimUtils.bytes(val);
            size = SimUtils.size(bytes);
            buffer.put(SimUtils.DOLLAR);
            buffer.put(size);
            buffer.put(SimUtils.CRLF);
            buffer.put(bytes);
            buffer.put(SimUtils.CRLF);
        }
    }

    public void longValue(int val) {
        byte[] bytes = SimUtils.bytes(val);
        byte[] size = SimUtils.size(bytes);
        buffer = ByteBuffer.allocate(bytes.length + size.length + 5);
        buffer.put(SimUtils.DOLLAR);
        buffer.put(size);
        buffer.put(SimUtils.CRLF);
        buffer.put(bytes);
        buffer.put(SimUtils.CRLF);
    }

    public void longList(long[] list) {
        byte[] size = SimUtils.size(list.length);
        buffer = ByteBuffer.allocate(1024 * list.length + 1024);
        buffer.put(SimUtils.STAR);
        buffer.put(size);
        buffer.put(SimUtils.CRLF);
        for (long val : list) {
            byte[] bytes = SimUtils.bytes(val);
            size = SimUtils.size(bytes);
            buffer.put(SimUtils.DOLLAR);
            buffer.put(size);
            buffer.put(SimUtils.CRLF);
            buffer.put(bytes);
            buffer.put(SimUtils.CRLF);
        }
    }

    public void floatValue(float val) {
        byte[] bytes = SimUtils.bytes(val);
        byte[] size = SimUtils.size(bytes);
        buffer = ByteBuffer.allocate(bytes.length + size.length + 5);
        buffer.put(SimUtils.DOLLAR);
        buffer.put(size);
        buffer.put(SimUtils.CRLF);
        buffer.put(bytes);
        buffer.put(SimUtils.CRLF);
    }

    public void floatList(float[] list) {
        byte[] size = SimUtils.size(list.length);
        buffer = ByteBuffer.allocate(1024 * list.length + 1024);
        buffer.put(SimUtils.STAR);
        buffer.put(size);
        buffer.put(SimUtils.CRLF);
        for (float val : list) {
            byte[] bytes = SimUtils.bytes(val);
            size = SimUtils.size(bytes);
            buffer.put(SimUtils.DOLLAR);
            buffer.put(size);
            buffer.put(SimUtils.CRLF);
            buffer.put(bytes);
            buffer.put(SimUtils.CRLF);
        }
    }

    public void stringValue(String val) {
        byte[] bytes = SimUtils.bytes(val);
        byte[] size = SimUtils.size(bytes);
        buffer = ByteBuffer.allocate(bytes.length + size.length + 5);
        buffer.put(SimUtils.DOLLAR);
        buffer.put(size);
        buffer.put(SimUtils.CRLF);
        buffer.put(bytes);
        buffer.put(SimUtils.CRLF);
    }

    public void stringList(String[] list) {
        byte[] size = SimUtils.size(list.length);
        buffer = ByteBuffer.allocate(1024 * list.length + 1024);
        buffer.put(SimUtils.STAR);
        buffer.put(size);
        buffer.put(SimUtils.CRLF);
        for (String str : list) {
            byte[] bytes = SimUtils.bytes(str);
            size = SimUtils.size(bytes);
            buffer.put(SimUtils.DOLLAR);
            buffer.put(size);
            buffer.put(SimUtils.CRLF);
            buffer.put(bytes);
            buffer.put(SimUtils.CRLF);
        }
    }

    public abstract void response();

}
