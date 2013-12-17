package com.guokr.simbase.errors;

public class SimEngineException extends SimException {

    private static final long serialVersionUID = 6830663864818521600L;

    public SimEngineException(String msg) {
        super(msg);
    }

    public SimEngineException(Throwable t) {
        super(t);
    }

    public SimEngineException(String msg, Throwable t) {
        super(msg, t);
    }

    public SimEngineException(String msg, Throwable t, String... context) {
        super(msg, t, context);
    }
}
