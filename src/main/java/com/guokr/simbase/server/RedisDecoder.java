package com.guokr.simbase.server;

import com.guokr.simbase.errors.server.LineTooLargeException;
import com.guokr.simbase.errors.server.ProtocolException;
import com.guokr.simbase.errors.server.RequestTooLargeException;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class RedisDecoder {

    public enum State {
        // for Redis protocol
        ALL_READ, READ_STAR, READ_DOLLAR,
        // for customized SimBase protocol
        READ_DOT, RAED_SPACE, READ_INTEGER, READ_FLOAT
    }

    private State            state         = State.READ_STAR;

    // bytes count need read
    private int              readRemaining = 0;

    // bytes count already read
    private int              readCount     = 0;

    RedisRequests            requests;

    private final int        maxBody;
    private final LineReader lineReader;

    public RedisDecoder(int maxBody, int maxLine) {
        this.maxBody = maxBody;
        this.lineReader = new LineReader(maxLine);
    }

    public RedisRequests decode(ByteBuffer buffer) throws LineTooLargeException, ProtocolException, RequestTooLargeException {
        @SuppressWarnings("unused")
        byte discriminator;
        while (buffer.hasRemaining()) {
            switch (state) {
            case ALL_READ:
                return requests;
            case READ_STAR:
                discriminator = lineReader.readByte(buffer);
                break;
            case READ_DOLLAR:
                discriminator = lineReader.readByte(buffer);
                break;
            default:
                break;
            }
        }
        return state == State.ALL_READ ? requests : null;
    }

    private void finish() {
        state = State.ALL_READ;
    }

    public void reset() {
        state = State.READ_STAR;
        readCount = 0;
        lineReader.reset();
        requests = null;
    }

}
