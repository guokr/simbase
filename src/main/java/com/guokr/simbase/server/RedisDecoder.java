package com.guokr.simbase.server;

import java.nio.ByteBuffer;

import com.guokr.simbase.SimUtils;
import com.guokr.simbase.errors.LineTooLargeException;
import com.guokr.simbase.errors.ProtocolException;

public class RedisDecoder {

    public enum State {
        INITIAL, READ_BEGIN, READ_NARGS, READ_ARGUMENT, READ_NBYTES, READ_STRING, READ_END
    }

    private State        state;
    private LineReader   lineReader;

    public RedisRequests requests;

    public static byte readDiscr(String line) {
        return line.getBytes()[0];
    }

    public static int readSize(String line) {
        int number = 0, idx = 0, len = line.length();
        while (idx < len - 1) {
            int next = line.charAt(++idx);
            int digit = next - SimUtils.ZERO;
            if (digit >= 0 && digit < 10) {
                number = number * 10 + digit;
            } else {
                String msg = String.format("Invalid character in the section for size: '%c'", next);
                throw new IllegalStateException(msg);
            }
        }

        return number;
    }

    public static String readString(String line, int nbytes) {
        int len = 0, length = line.length();
        StringBuilder builder = new StringBuilder();
        for (int idx = 0; idx < length && len < nbytes; idx++) {
            int c = line.codePointAt(idx);
            if (0x0000 <= c && c < 0x0080) {
                len += 1;
                builder.append(line.charAt(idx));
            } else if (0x0080 <= c && c < 0x0800) {
                len += 2;
                builder.append(line.charAt(idx));
            } else if (0x0800 <= c && c < 0x10000) {
                len += 3;
                builder.append(line.charAt(idx));
            } else if (0x10000 <= c && c < 0x200000) {
                len += 4;
                builder.append(line.charAt(idx++));
                builder.append(line.charAt(idx));
            } else if (0x200000 <= c && c < 0x4000000) {
                len += 5;
                builder.append(line.charAt(idx++));
                builder.append(line.charAt(idx));
            } else if (0x4000000 <= c && c < 0x80000000) {
                len += 6;
                builder.append(line.charAt(idx++));
                builder.append(line.charAt(idx));
            }
        }
        if (len > nbytes) {
            String msg = String.format("Invalid string legnth[%d] in the line: '%s'", len, line);
            throw new IllegalStateException(msg);
        }
        return builder.toString();
    }

    public RedisRequests decode(ByteBuffer buffer, RedisRequests last) throws ProtocolException, LineTooLargeException {
        byte discr;
        String line = null;
        state = State.INITIAL;
        int nargs = 0, nbytes = 0;

        if (last != null) {
            state = last.state;
            nargs = last.nargs;
            nbytes = last.nbytes;
            line = last.line;
            lineReader = last.lineReader;
            requests = last;
            if (state == null) {
                state = State.INITIAL;
            }
        }

        while (buffer.hasRemaining()) {
            switch (state) {
            case READ_END:
                return requests;
            case INITIAL:
                lineReader = new LineReader();
                line = lineReader.readLine(buffer);
                if (line != null && line.length() != 0) {
                    if (last == null) {
                        requests = new RedisRequests();
                    }
                    state = State.READ_BEGIN;
                }
                break;
            case READ_BEGIN:
                if (line != null && line.length() != 0) {
                    discr = readDiscr(line);
                    if (discr == SimUtils.STAR) {
                        state = State.READ_NARGS;
                    } else {
                        String msg = String.format("wrong byte %s('%c')", discr, discr);
                        throw new ProtocolException(msg);
                    }
                }
                break;
            case READ_NARGS:
                if (line != null && line.length() != 0) {
                    nargs = readSize(line);
                    if (nargs < 1) {
                        throw new ProtocolException();
                    }
                    state = State.READ_ARGUMENT;
                    requests.request(nargs);
                    line = null;
                }
                break;
            case READ_ARGUMENT:
                if (nargs > 0) {
                    line = lineReader.readLine(buffer);
                    if (line != null && line.length() != 0) {
                        discr = readDiscr(line);
                        if (discr == SimUtils.DOLLAR) {
                            state = State.READ_NBYTES;
                        }
                    }
                } else if (nargs == 0) {
                    state = State.READ_END;
                } else {
                    throw new ProtocolException();
                }
                break;
            case READ_NBYTES:
                if (line != null && line.length() != 0) {
                    nbytes = readSize(line);
                    state = State.READ_STRING;
                    line = null;
                }
                break;
            case READ_STRING:
                line = lineReader.readLine(buffer);
                if (line != null && line.length() != 0) {
                    requests.add(readString(line, nbytes));
                    nargs--;
                    state = State.READ_ARGUMENT;
                    line = null;
                }
                break;
            }
            // System.out.println("current - state:" + state + ", nargs:" +
            // nargs);
        }

        if (state == State.READ_ARGUMENT && nargs == 0) {
            state = State.READ_END;
        }

        if (state == State.READ_END) {
            requests.isFinished = true;
            requests.state = State.INITIAL;
            requests.nargs = 0;
            requests.nbytes = 0;
            requests.line = null;
            requests.lineReader = null;
            // System.out.println("final - state:" + state + ", nargs:" +
            // nargs);
        } else {
            requests.isFinished = false;
            requests.state = state;
            requests.nargs = nargs;
            requests.nbytes = nbytes;
            requests.line = line;
            requests.lineReader = lineReader;
            // System.out.println("last - state:" + state + ", nargs:" + nargs);
        }

        return requests;
    }

    public void reset() {
        state = State.INITIAL;
        requests = null;
        lineReader.reset();
    }

}
