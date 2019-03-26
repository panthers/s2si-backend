package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 */
public class PRTokenExpiredException extends Exception {

	private static final long serialVersionUID = 6411559565532727430L;

	public PRTokenExpiredException() {
	}

	public PRTokenExpiredException(String message) {
		super(message);
	}

	public PRTokenExpiredException(Throwable cause) {
		super(cause);
	}

	public PRTokenExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

	public PRTokenExpiredException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
