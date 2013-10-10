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
        // start point
        READ_BEGIN, READ_NARGS,
        // for Redis protocol - argument
        READ_ARGUMENT_BEGIN, READ_NBYTES, READ_ARGUMENT, READ_STRING,
        // for customized SimBase protocol - control character
        TRY_DOT, TRY_DOTDOT, READ_NINTS, READ_NFLTS, RAED_SPACE,
        // for customized SimBase protocol - data
        READ_INTEGER, READ_FLOAT,
        // end point
        READ_END
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
        int nargs = 0, nbytes = 0, nnums;
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
                nargs = lineReader.readSizeByLine(buffer);
                state = State.READ_ARGUMENT;
                break;
            case READ_ARGUMENT:
                discr = lineReader.readByte(buffer);
                if (nargs > 0 && discr == RedisUtils.DOLLAR) {
                    state = State.READ_NBYTES;
                    nargs--;
                } else {
                    throw new ProtocolException();
                }
                break;
            case READ_NBYTES:
                nbytes = lineReader.readSizeByLine(buffer);
                lineReader.begin();
                byte first = lineReader.tryByte(buffer);
                byte second = lineReader.tryByte(buffer);
                if (first == RedisUtils.DOT) {
                    if (second == RedisUtils.DOT) {
                        lineReader.commit();
                        state = State.READ_NFLTS;
                    } else {
                        lineReader.commit();
                        state = State.READ_NINTS;
                    }
                } else {
                    lineReader.rollback();
                    state = State.READ_STRING;
                }
                break;
            case READ_NFLTS:
                break;
            case READ_NINTS:
                break;
            case READ_INTEGER:
                break;
            case READ_FLOAT:
                break;
            case READ_STRING:
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
