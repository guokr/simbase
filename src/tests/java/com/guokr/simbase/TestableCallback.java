package com.guokr.simbase;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

public abstract class TestableCallback extends SimCallback {

    public abstract void validator();

    public static TestableCallback noop() {
        return new TestableCallback() {
            @Override
            public void validator() {
            }
        };
    }

    @Override
    public void response() {
        validator();
    }

    public void isOk() {
        ByteBuffer excepted = ByteBuffer.allocate(5);
        excepted.put(SimUtils.PLUS);
        excepted.put(SimUtils.OK);
        excepted.put(SimUtils.CRLF);
        assertEquals(buffer.compact().toString(), excepted.toString());
    }

    public void isOk(String msg) {
        byte[] bytes = SimUtils.bytes(msg);
        ByteBuffer excepted = ByteBuffer.allocate(bytes.length + 3);
        excepted.put(SimUtils.PLUS);
        excepted.put(bytes);
        excepted.put(SimUtils.CRLF);
        assertEquals(buffer.compact().toString(), excepted.toString());
    }

    public void isError(String msg) {
        byte[] bytes = SimUtils.bytes(msg);
        ByteBuffer excepted = ByteBuffer.allocate(bytes.length + 3);
        excepted.put(SimUtils.MINUS);
        excepted.put(bytes);
        excepted.put(SimUtils.CRLF);
        assertEquals(buffer.compact().toString(), excepted.toString());
    }

    public void isStatus(int code) {
        byte[] bytes = SimUtils.bytes(code);
        ByteBuffer excepted = ByteBuffer.allocate(bytes.length + 3);
        excepted.put(SimUtils.COLON);
        excepted.put(bytes);
        excepted.put(SimUtils.CRLF);
        assertEquals(buffer.compact().toString(), excepted.toString());
    }

    public void isIntegerValue(int val) {
        byte[] bytes = SimUtils.bytes(val);
        byte[] size = SimUtils.size(bytes);
        ByteBuffer excepted = ByteBuffer.allocate(bytes.length + size.length + 5);
        excepted.put(SimUtils.DOLLAR);
        excepted.put(size);
        excepted.put(SimUtils.CRLF);
        excepted.put(bytes);
        excepted.put(SimUtils.CRLF);
        assertEquals(buffer.compact().toString(), excepted.toString());
    }

    public void isIntegerList(int[] list) {
        byte[] size = SimUtils.size(list.length);
        ByteBuffer excepted = ByteBuffer.allocate(1024 * list.length + 1024);
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

        excepted.flip();
        byte[] dstExp = new byte[excepted.limit()];
        excepted.get(dstExp);
        String a = new String(dstExp);
        System.out.println(a);
        excepted.flip();

        buffer.flip();
        byte[] dstBuf = new byte[buffer.limit()];
        buffer.get(dstBuf);
        String b = new String(dstBuf);
        System.out.println(b);
        buffer.flip();

        assertEquals(a, b);
    }

    public void isFloatValue(float val) {
        byte[] bytes = SimUtils.bytes(val);
        byte[] size = SimUtils.size(bytes);
        ByteBuffer excepted = ByteBuffer.allocate(bytes.length + size.length + 5);
        excepted.put(SimUtils.DOLLAR);
        excepted.put(size);
        excepted.put(SimUtils.CRLF);
        excepted.put(bytes);
        excepted.put(SimUtils.CRLF);
        assertEquals(buffer.compact().toString(), excepted.toString());
    }

    public void isFloatList(float[] list) {
        byte[] size = SimUtils.size(list.length);
        ByteBuffer excepted = ByteBuffer.allocate(1024 * list.length + 1024);
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
        assertEquals(buffer.compact().toString(), excepted.toString());
    }

    public void isStringValue(String val) {
        byte[] bytes = SimUtils.bytes(val);
        byte[] size = SimUtils.size(bytes);
        ByteBuffer excepted = ByteBuffer.allocate(bytes.length + size.length + 5);
        excepted.put(SimUtils.DOLLAR);
        excepted.put(size);
        excepted.put(SimUtils.CRLF);
        excepted.put(bytes);
        excepted.put(SimUtils.CRLF);
        assertEquals(buffer.compact().toString(), excepted.toString());
    }

    public void isStringList(String[] list) {
        byte[] size = SimUtils.size(list.length);
        ByteBuffer excepted = ByteBuffer.allocate(1024 * list.length + 1024);
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
        assertEquals(buffer.compact().toString(), excepted.toString());
    }
}
