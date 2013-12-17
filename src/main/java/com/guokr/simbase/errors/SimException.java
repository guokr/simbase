package com.guokr.simbase.errors;

import java.util.HashMap;
import java.util.Map;

public class SimException extends RuntimeException {

    private static final long   serialVersionUID = -4170061639543762537L;
    private Map<String, String> context;

    public SimException(String msg) {
        super(msg);
    }

    public SimException(Throwable t) {
        super(t);
    }

    public SimException(String msg, Throwable t) {
        super(msg, t);
    }

    public SimException(String msg, Throwable t, String... pairs) {
        super(msg, t);
        context = new HashMap<String, String>();
        int length = pairs.length;
        if (length % 2 != 0) {
            throw new IllegalArgumentException("Exception context should be paired");
        }
        for (int i = 0; i < length;) {
            context.put(pairs[i], pairs[i + 1]);
            i += 2;
        }
    }
}
