package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 */
public class JWTBadSignException extends Exception {

	private static final long serialVersionUID = 1250069688699499597L;

	public JWTBadSignException() {
	}

	public JWTBadSignException(String message) {
		super(message);
	}

	public JWTBadSignException(Throwable cause) {
		super(cause);
	}

	public JWTBadSignException(String message, Throwable cause) {
		super(message, cause);
	}

	public JWTBadSignException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
