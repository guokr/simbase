package com.guokr.simbase.server;

import java.nio.ByteBuffer;

public class LineReader {

    public LineReader() {
    }

    public void reset() {
    }

    public void begin() {
    }

    public void commit() {
    }

    public void rollback() {
    }

    public byte readByte(ByteBuffer buffer) {
        return 0;
    }

    public byte tryByte(ByteBuffer buffer) {
        return 0;
    }

    public int readSizeBy(ByteBuffer buffer, byte[] delims) {
        return 0;
    }

    public int readIntegerBy(ByteBuffer buffer, byte[] delims) {
        return 0;
    }

    public float readFloatBy(ByteBuffer buffer, byte[] delims) {
        return 0;
    }

    public String readStringBy(ByteBuffer buffer, byte[] delims, int nbytes) {
        return null;
    }

}
