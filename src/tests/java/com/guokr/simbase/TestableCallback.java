package com.guokr.simbase;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

public abstract class TestableCallback extends SimCallback {

    protected ByteBuffer excepted;

    public abstract void excepted();

    public static TestableCallback noop() {
        return new TestableCallback() {
            @Override
            public void excepted() {
            }
        };
    }

    @Override
    public void response() {
        excepted();
    }

    public void isOk() {
        excepted = ByteBuffer.allocate(5);
        excepted.put(SimUtils.PLUS);
        excepted.put(SimUtils.OK);
        excepted.put(SimUtils.CRLF);
    }

    public void isOk(String msg) {
        byte[] bytes = SimUtils.bytes(msg);
        excepted = ByteBuffer.allocate(bytes.length + 3);
        excepted.put(SimUtils.PLUS);
        excepted.put(bytes);
        excepted.put(SimUtils.CRLF);
    }

    public void isError(String msg) {
        byte[] bytes = SimUtils.bytes(msg);
        excepted = ByteBuffer.allocate(bytes.length + 3);
        excepted.put(SimUtils.MINUS);
        excepted.put(bytes);
        excepted.put(SimUtils.CRLF);
    }

    public void isStatus(int code) {
        byte[] bytes = SimUtils.bytes(code);
        excepted = ByteBuffer.allocate(bytes.length + 3);
        excepted.put(SimUtils.COLON);
        excepted.put(bytes);
        excepted.put(SimUtils.CRLF);
    }

    public void isIntegerValue(int val) {
        byte[] bytes = SimUtils.bytes(val);
        byte[] size = SimUtils.size(bytes);
        excepted = ByteBuffer.allocate(bytes.length + size.length + 5);
        excepted.put(SimUtils.DOLLAR);
        excepted.put(size);
        excepted.put(SimUtils.CRLF);
        excepted.put(bytes);
        excepted.put(SimUtils.CRLF);
    }

    public void isIntegerList(int[] list) {
        byte[] size = SimUtils.size(list.length);
        excepted = ByteBuffer.allocate(1024 * list.length + 1024);
        excepted.put(SimUtils.STAR);
        excepted.put(size);
        excepted.put(SimUtils.CRLF);
        for (int val : list) {
            byte[] bytes = SimUtils.bytes(val);
            size = SimUtils.size(bytes);
            excepted.put(SimUtils.DOLLAR);
            excepted.put(size);
            excepted.put(SimUtils.CRLF);
            excepted.put(bytes);
            excepted.put(SimUtils.CRLF);
        }
    }

    public void isLongValue(long val) {
        byte[] bytes = SimUtils.bytes(val);
        byte[] size = SimUtils.size(bytes);
        excepted = ByteBuffer.allocate(bytes.length + size.length + 5);
        excepted.put(SimUtils.DOLLAR);
        excepted.put(size);
        excepted.put(SimUtils.CRLF);
        excepted.put(bytes);
        excepted.put(SimUtils.CRLF);
    }

    public void isLongList(long[] list) {
        byte[] size = SimUtils.size(list.length);
        excepted = ByteBuffer.allocate(1024 * list.length + 1024);
        excepted.put(SimUtils.STAR);
        excepted.put(size);
        excepted.put(SimUtils.CRLF);
        for (long val : list) {
            byte[] bytes = SimUtils.bytes(val);
            size = SimUtils.size(bytes);
            excepted.put(SimUtils.DOLLAR);
            excepted.put(size);
            excepted.put(SimUtils.CRLF);
            excepted.put(bytes);
            excepted.put(SimUtils.CRLF);
        }
    }

    public void isFloatValue(float val) {
        byte[] bytes = SimUtils.bytes(val);
        byte[] size = SimUtils.size(bytes);
        excepted = ByteBuffer.allocate(bytes.length + size.length + 5);
        excepted.put(SimUtils.DOLLAR);
        excepted.put(size);
        excepted.put(SimUtils.CRLF);
        excepted.put(bytes);
        excepted.put(SimUtils.CRLF);
    }

    public void isFloatList(float[] list) {
        byte[] size = SimUtils.size(list.length);
        excepted = ByteBuffer.allocate(1024 * list.length + 1024);
        excepted.put(SimUtils.STAR);
        excepted.put(size);
        excepted.put(SimUtils.CRLF);
        for (float val : list) {
            byte[] bytes = SimUtils.bytes(val);
            size = SimUtils.size(bytes);
            excepted.put(SimUtils.DOLLAR);
            excepted.put(size);
            excepted.put(SimUtils.CRLF);
            excepted.put(bytes);
            excepted.put(SimUtils.CRLF);
        }
    }

    public void isStringValue(String val) {
        byte[] bytes = SimUtils.bytes(val);
        byte[] size = SimUtils.size(bytes);
        excepted = ByteBuffer.allocate(bytes.length + size.length + 5);
        excepted.put(SimUtils.DOLLAR);
        excepted.put(size);
        excepted.put(SimUtils.CRLF);
        excepted.put(bytes);
        excepted.put(SimUtils.CRLF);
    }

    public void isStringList(String[] list) {
        byte[] size = SimUtils.size(list.length);
        excepted = ByteBuffer.allocate(1024 * list.length + 1024);
        excepted.put(SimUtils.STAR);
        excepted.put(size);
        excepted.put(SimUtils.CRLF);
        for (String str : list) {
            byte[] bytes = SimUtils.bytes(str);
            size = SimUtils.size(bytes);
            excepted.put(SimUtils.DOLLAR);
            excepted.put(size);
            excepted.put(SimUtils.CRLF);
            excepted.put(bytes);
            excepted.put(SimUtils.CRLF);
        }
    }

    public void waitForFinish() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void validate() {
        excepted.flip();
        byte[] dstExp = new byte[excepted.limit()];
        excepted.get(dstExp);
        String exp = new String(dstExp);
        excepted.flip();

        buffer.flip();
        byte[] dstBuf = new byte[buffer.limit()];
        buffer.get(dstBuf);
        String fact = new String(dstBuf);
        buffer.flip();

        assertEquals("assertEquals", exp, fact);
    }

}
