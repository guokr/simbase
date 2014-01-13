package com.guokr.simbase.errors;

public class ProtocolException extends Exception {

    private static final long serialVersionUID = -6423107118444074895L;

    public ProtocolException() {
    }

    public ProtocolException(String msg) {
        super(msg);
    }

    public ProtocolException(Throwable t) {
        super(t);
    }

    public ProtocolException(String msg, Throwable t) {
        super(msg, t);
    }

}
