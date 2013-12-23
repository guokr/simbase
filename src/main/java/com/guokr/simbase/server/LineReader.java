package com.guokr.simbase.server;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.guokr.simbase.SimUtils;
import com.guokr.simbase.errors.LineTooLargeException;

public class LineReader {
    // 1k buffer, increase as necessary;
    byte[]            lineBuffer    = new byte[1024];
    int               lineBufferIdx = 0;
    private final int maxLine       = 64 * 1024;

    public String readLine(ByteBuffer buffer) throws LineTooLargeException {
        byte b;
        boolean more = true;
        while (buffer.hasRemaining() && more) {
            b = buffer.get();
            if (b == SimUtils.CR) {
                if (buffer.hasRemaining() && buffer.get() == SimUtils.LF) {
                    more = false;
                }
            } else if (b == SimUtils.LF) {
                more = false;
            } else {
                if (lineBufferIdx == maxLine - 2) {
                    throw new LineTooLargeException("exceed max line " + maxLine);
                }
                if (lineBufferIdx == lineBuffer.length) {
                    lineBuffer = Arrays.copyOf(lineBuffer, lineBuffer.length * 2);
                }
                lineBuffer[lineBufferIdx] = b;
                ++lineBufferIdx;
            }
        }
        String line = null;
        if (!more) {
            line = new String(lineBuffer, 0, lineBufferIdx);
            lineBufferIdx = 0;
        }
        return line;
    }

    public final void reset() {
        this.lineBufferIdx = 0;
    }
}
