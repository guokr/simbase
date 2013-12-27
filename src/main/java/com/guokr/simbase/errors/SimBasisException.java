package com.guokr.simbase.errors;

public class SimBasisException extends SimException {

    private static final long serialVersionUID = 6830663864818521600L;

    public SimBasisException(String msg) {
        super(msg);
    }

    public SimBasisException(Throwable t) {
        super(t);
    }

    public SimBasisException(String msg, Throwable t) {
        super(msg, t);
    }
}
