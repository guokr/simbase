package com.guokr.simbase.server;

import java.nio.ByteBuffer;

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
                if (discr == RedisUtils.STAR) {
                    state = State.READ_NARGS;
                } else {
                    String msg = String.format("wrong byte %s('%c')", discr, discr);
                    throw new ProtocolException(msg);
                }
                break;
            case READ_NARGS:
                nargs = lineReader.readSizeBy(buffer, RedisUtils.CRLF);
                if (nargs < 1) {
                    throw new ProtocolException();
                }
                state = State.READ_ARGUMENT;
                requests.request(nargs);
                break;
            case READ_ARGUMENT:
                discr = lineReader.readByte(buffer);
                if (nargs > 0 && discr == RedisUtils.DOLLAR) {
                    state = State.READ_NBYTES;
                    nargs--;
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
                nbytes = lineReader.readSizeBy(buffer, RedisUtils.CRLF);
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
                    requests.string(nbytes);
                }
                break;
            case READ_NINTS:
                nnums = lineReader.readSizeBy(buffer, RedisUtils.SPACE);
                state = State.READ_INTEGER;
                requests.intarray(nnums);
                break;
            case READ_NFLTS:
                nnums = lineReader.readSizeBy(buffer, RedisUtils.SPACE);
                state = State.READ_FLOAT;
                requests.floatarray(nnums);
                break;
            case READ_INTEGER:
                if (nnums > 1) {
                    requests.arrayadd(lineReader.readIntegerBy(buffer, RedisUtils.SPACE));
                    nnums--;
                } else if (nnums == 1) {
                    requests.arrayadd(lineReader.readIntegerBy(buffer, RedisUtils.CRLF));
                    nnums--;
                    state = State.READ_ARGUMENT;
                }
                break;
            case READ_FLOAT:
                if (nnums > 1) {
                    requests.arrayadd(lineReader.readFloatBy(buffer, RedisUtils.SPACE));
                    nnums--;
                } else if (nnums == 1) {
                    requests.arrayadd(lineReader.readFloatBy(buffer, RedisUtils.CRLF));
                    nnums--;
                    state = State.READ_ARGUMENT;
                }
                break;
            case READ_STRING:
                requests.set(lineReader.readStringBy(buffer, RedisUtils.CRLF, nbytes));
                state = State.READ_ARGUMENT;
                break;
            default:
                break;
            }
        }
        return state == State.READ_END ? requests : null;
    }

}
