package com.guokr.simbase.server;

import java.nio.ByteBuffer;

import com.guokr.simbase.SimUtils;
import com.guokr.simbase.errors.ProtocolException;

public class RedisDecoder {

    public enum State {
        READ_BEGIN, READ_NARGS, READ_ARGUMENT, READ_NBYTES, READ_STRING, READ_END
    }

    private State            state = State.READ_BEGIN;

    RedisRequests            requests;

    private final LineReader lineReader;

    public RedisDecoder() {
        this.lineReader = new LineReader();
    }

    public RedisRequests decode(ByteBuffer buffer) throws ProtocolException {
        byte discr;
        int nargs = 0, nbytes = 0;
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
                state = State.READ_STRING;
                break;
            case READ_STRING:
                requests.add(lineReader.readStringBy(buffer, SimUtils.CRLF, nbytes));
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
