package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 */
public class PRTokenIssueException extends Exception {

	private static final long serialVersionUID = -5025090060189470514L;

	public PRTokenIssueException() {
	}

	public PRTokenIssueException(String message) {
		super(message);
	}

	public PRTokenIssueException(Throwable cause) {
		super(cause);
	}

	public PRTokenIssueException(String message, Throwable cause) {
		super(message, cause);
	}

	public PRTokenIssueException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
