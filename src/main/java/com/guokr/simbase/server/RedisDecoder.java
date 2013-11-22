package com.guokr.simbase.server;

import java.nio.ByteBuffer;

import com.guokr.simbase.SimUtils;
import com.guokr.simbase.errors.server.ProtocolException;

public class RedisDecoder {

    public enum State {
        // start point
        READ_BEGIN, READ_NARGS,
        // for Redis protocol - argument
        READ_NBYTES, READ_ARGUMENT, READ_STRING,
        // for customized SimBase protocol - control character
        TRY_DOT, TRY_DOTDOT, READ_NINTS, READ_NFLTS, RAED_SPACE,
        // for customized SimBase protocol - data
        READ_INTEGER, READ_FLOAT,
        // end point
        READ_END
    }

    private State            state = State.READ_BEGIN;

    RedisRequests            requests;

    private final LineReader lineReader;

    public RedisDecoder() {
        this.lineReader = new LineReader();
    }

    public RedisRequests decode(ByteBuffer buffer) throws ProtocolException {
        byte discr;
        int nargs = 0, nbytes = 0, nnums = 0;
        requests = new RedisRequests();
        while (buffer.hasRemaining()) {
            switch (state) {
            case READ_END:
                return requests;
            case READ_BEGIN:
                discr = lineReader.readByte(buffer);
                if (discr == SimUtils.STAR) {
                    state = State.READ_NARGS;
                } else {
                    String msg = String.format("wrong byte %s('%c')", discr, discr);
                    throw new ProtocolException(msg);
                }
                break;
            case READ_NARGS:
                nargs = lineReader.readSizeBy(buffer, SimUtils.CRLF);
                if (nargs < 1) {
                    throw new ProtocolException();
                }
                state = State.READ_ARGUMENT;
                requests.request(nargs);
                break;
            case READ_ARGUMENT:
                if (nargs > 0) {
                    discr = lineReader.readByte(buffer);
                    if (discr == SimUtils.DOLLAR) {
                        state = State.READ_NBYTES;
                        nargs--;
                    }
                } else if (nargs == 0) {
                    if (buffer.hasRemaining()) {
                        state = State.READ_STRING;
                    } else {
                        state = State.READ_END;
                    }
                } else {
                    throw new ProtocolException();
                }
                break;
            case READ_NBYTES:
                nbytes = lineReader.readSizeBy(buffer, SimUtils.CRLF);
                lineReader.begin();
                byte first = lineReader.tryByte(buffer);
                byte second = lineReader.tryByte(buffer);
                if (first == SimUtils.DOT) {
                    if (second == SimUtils.DOT) {
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
            case READ_NINTS:
                nnums = lineReader.readSizeBy(buffer, SimUtils.SPACE);
                state = State.READ_INTEGER;
                requests.list(nnums);
                break;
            case READ_NFLTS:
                nnums = lineReader.readSizeBy(buffer, SimUtils.SPACE);
                state = State.READ_FLOAT;
                requests.list(nnums);
                break;
            case READ_INTEGER:
                if (nnums > 1) {
                    requests.add(lineReader.readIntegerBy(buffer, SimUtils.SPACE));
                    nnums--;
                } else if (nnums == 1) {
                    requests.add(lineReader.readIntegerBy(buffer, SimUtils.CRLF));
                    nnums--;
                    state = State.READ_ARGUMENT;
                }
                break;
            case READ_FLOAT:
                if (nnums > 1) {
                    requests.add(lineReader.readFloatBy(buffer, SimUtils.SPACE));
                    nnums--;
                } else if (nnums == 1) {
                    requests.add(lineReader.readFloatBy(buffer, SimUtils.CRLF));
                    nnums--;
                    state = State.READ_ARGUMENT;
                }
                break;
            case READ_STRING:
                requests.set(lineReader.readStringBy(buffer, SimUtils.CRLF, nbytes));
                state = State.READ_ARGUMENT;
                break;
            default:
                break;
            }
        }

        if (state == State.READ_ARGUMENT) {
            state = State.READ_END;
        }
        return state == State.READ_END ? requests : null;
    }

    public void reset() {
        state = State.READ_BEGIN;
        lineReader.reset();
    }

}
