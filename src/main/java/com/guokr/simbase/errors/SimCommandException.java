package com.guokr.simbase.errors;

public class SimCommandException extends SimException {

    private static final long serialVersionUID = 8529211973276461863L;

    public SimCommandException(String msg) {
        super(msg);
    }

    public SimCommandException(Throwable t) {
        super(t);
    }

    public SimCommandException(String msg, Throwable t) {
        super(msg, t);
    }

    public SimCommandException(String msg, Throwable t, String... context) {
        super(msg, t, context);
    }
}
