package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 */
public class JWTNotATokenException extends Exception {

	private static final long serialVersionUID = -5024255106379792004L;

	public JWTNotATokenException() {
	}

	public JWTNotATokenException(String message) {
		super(message);
	}

	public JWTNotATokenException(Throwable cause) {
		super(cause);
	}

	public JWTNotATokenException(String message, Throwable cause) {
		super(message, cause);
	}

	public JWTNotATokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
