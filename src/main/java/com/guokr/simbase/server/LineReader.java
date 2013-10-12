package com.guokr.simbase.server;

import java.nio.ByteBuffer;

import com.guokr.simbase.SimUtils;

public class LineReader {

    int index = 0, mark = -1;

    public LineReader() {
    }

    public void reset() {
        index = 0;
        mark = -1;
    }

    public void begin() {
        if (mark != -1) {
            throw new IllegalStateException("embeded transcation is not allowed!");
        }
        mark = index;
    }

    public void commit() {
        if (mark != -1) {
            mark = -1;
        }
    }

    public void rollback() {
        if (mark != -1) {
            index = mark;
        }
    }

    public int trySame(byte[] checked, ByteBuffer buffer, int index) {
        int sameCount = 0, len = checked.length;
        while (sameCount < len && buffer.get(index + sameCount) == checked[sameCount]) {
            sameCount++;
        }
        return sameCount;
    }

    public byte readByte(ByteBuffer buffer) {
        byte result = buffer.get(index);
        if (mark == -1) {
            buffer.position(index++);
        }
        return result;
    }

    public byte tryByte(ByteBuffer buffer) {
        index++;
        return buffer.get(index);
    }

    public int readSizeBy(ByteBuffer buffer, byte[] delims) {
        int number = 0;
        int next = buffer.get(index), len = delims.length;
        while (true) {
            if (next == -1) {
                throw new IllegalStateException("Unexpected end");
            } else if (trySame(delims, buffer, index) == len) {
                index = index + len;
                break;
            }

            int digit = next - SimUtils.ZERO;
            if (digit >= 0 && digit < 10) {
                number = number * 10 + digit;
            } else {
                String msg = String.format("Invalid character in the section for size: '%c'", next);
                throw new IllegalStateException(msg);
            }
            next = buffer.get(index++);
        }

        if (mark == -1) {
            buffer.position(index);
        }

        return number;
    }

    public int readIntegerBy(ByteBuffer buffer, byte[] delims) {
        int sign = 1, len = delims.length, next = buffer.get(index);
        int number = 0;

        if (next == '-') {
            next = buffer.get(index++);
            sign = -1;
        }

        while (true) {
            if (next == -1) {
                throw new IllegalStateException("Unexpected end");
            } else if (trySame(delims, buffer, index) == len) {
                index = index + len;
                number = number * sign;
                break;
            }

            int digit = next - SimUtils.ZERO;
            if (digit >= 0 && digit < 10) {
                number = number * 10 + digit;
            } else {
                String msg = String.format("Invalid character in the section for size: '%c'", next);
                throw new IllegalStateException(msg);
            }
            next = buffer.get(index++);
        }

        if (mark == -1) {
            buffer.position(index);
        }

        return number;
    }

    public float readFloatBy(ByteBuffer buffer, byte[] delims) {
        return 0;
    }

    public String readStringBy(ByteBuffer buffer, byte[] delims, int nbytes) {
        return null;
    }

}
