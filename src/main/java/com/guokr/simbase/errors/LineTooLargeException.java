package com.guokr.simbase.errors;

public class LineTooLargeException extends RuntimeException {
    private static final long serialVersionUID = 4769605631935362555L;

    public LineTooLargeException(String msg) {
        super(msg);
    }

}
