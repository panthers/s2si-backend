package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 */
public class JWTSigningException extends Exception {

	private static final long serialVersionUID = -4783729939461130404L;

	public JWTSigningException() {
	}

	public JWTSigningException(String message) {
		super(message);
	}

	public JWTSigningException(Throwable cause) {
		super(cause);
	}

	public JWTSigningException(String message, Throwable cause) {
		super(message, cause);
	}

	public JWTSigningException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
