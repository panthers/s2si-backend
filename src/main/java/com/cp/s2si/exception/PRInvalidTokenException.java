package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 */
public class PRInvalidTokenException extends Exception {

	private static final long serialVersionUID = -3994786442645590452L;

	public PRInvalidTokenException() {
	}

	public PRInvalidTokenException(String message) {
		super(message);
	}

	public PRInvalidTokenException(Throwable cause) {
		super(cause);
	}

	public PRInvalidTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	public PRInvalidTokenException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
