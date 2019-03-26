package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 */
public class JWTEncryptionException extends Exception {

	private static final long serialVersionUID = -3370533296149281291L;

	public JWTEncryptionException() {
	}

	public JWTEncryptionException(String message) {
		super(message);
	}

	public JWTEncryptionException(Throwable cause) {
		super(cause);
	}

	public JWTEncryptionException(String message, Throwable cause) {
		super(message, cause);
	}

	public JWTEncryptionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
