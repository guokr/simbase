package com.guokr.simbase.errors;


public class SimException extends RuntimeException {

	private static final long serialVersionUID = -4170061639543762537L;

	public SimException(String msg) {
		super(msg);
	}

	public SimException(Throwable t) {
		super(t);
	}

	public SimException(String msg, Throwable t) {
		super(msg, t);
	}
}
