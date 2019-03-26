package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 */
public class JWTAudienceException extends Exception {

	private static final long serialVersionUID = -5467041437971639174L;

	public JWTAudienceException() {
	}

	public JWTAudienceException(String message) {
		super(message);
	}

	public JWTAudienceException(Throwable cause) {
		super(cause);
	}

	public JWTAudienceException(String message, Throwable cause) {
		super(message, cause);
	}

	public JWTAudienceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
