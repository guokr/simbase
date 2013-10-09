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
        READ_BEGIN, READ_END,
        // for Redis protocol
        READ_NARGS, READ_NBYTES, READ_BYTES,
        // for customized SimBase protocol - control character
        READ_DOT, READ_DOTDOT, READ_NNUMS, RAED_SPACE,
        // for customized SimBase protocol - data
        READ_INTEGER, READ_FLOAT
    }

    private State            state         = State.READ_BEGIN;

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
        byte discr;
        int nargs, nbytes, nnums;
        while (buffer.hasRemaining()) {
            switch (state) {
            case READ_END:
                return requests;
            case READ_BEGIN:
                discr = lineReader.readByte(buffer);
                if (discr == RedisUtils.STAR) {
                    state = State.READ_NARGS;
                } else {
                    throw new ProtocolException();
                }
            case READ_NARGS:
                nargs = lineReader.readSize(buffer);
                break;
            default:
                break;
            }
        }
        return state == State.READ_END ? requests : null;
    }

    private void finish() {
        state = State.READ_END;
    }

    public void reset() {
        state = State.READ_BEGIN;
        readCount = 0;
        lineReader.reset();
        requests = null;
    }

}
