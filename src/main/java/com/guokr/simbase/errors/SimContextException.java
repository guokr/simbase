package com.guokr.simbase.errors;

public class SimContextException extends SimException {

    private static final long serialVersionUID = 4295837255563564513L;

    public SimContextException(String msg) {
        super(msg);
    }

    public SimContextException(Throwable t) {
        super(t);
    }

    public SimContextException(String msg, Throwable t) {
        super(msg, t);
    }
}
