package com.guokr.simbase.server;

import java.nio.ByteBuffer;

public class LineReader {

    public LineReader(int maxLine) {
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

    public int readInteger(ByteBuffer buffer) {
        return 0;
    }

    public float readFloat(ByteBuffer buffer) {
        return 0;
    }

    public int readString(ByteBuffer buffer) {
        return 0;
    }

    public String readByLine(ByteBuffer buffer) {
        return null;
    }

    public int readSizeByLine(ByteBuffer buffer) {
        return 0;
    }

    public String readRemainingByLine(ByteBuffer buffer) {
        return null;
    }

}
