package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 */
public class JWTNoPayloadException extends Exception {

	private static final long serialVersionUID = -6782650099127196479L;

	public JWTNoPayloadException() {
	}

	public JWTNoPayloadException(String message) {
		super(message);
	}

	public JWTNoPayloadException(Throwable cause) {
		super(cause);
	}

	public JWTNoPayloadException(String message, Throwable cause) {
		super(message, cause);
	}

	public JWTNoPayloadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
