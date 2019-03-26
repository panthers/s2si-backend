package com.cp.s2si.exception;

/**
 * 
 * @author panther
 * 
 * This exception is thrown by processors when they have failed but would like the process to continue chaining.
 *
 */
public class MessageProcessingContinueException extends Exception {

	private static final long serialVersionUID = -2059555559759719654L;

	public MessageProcessingContinueException() {
	}

	public MessageProcessingContinueException(String message) {
		super(message);
	}

	public MessageProcessingContinueException(Throwable cause) {
		super(cause);
	}

	public MessageProcessingContinueException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageProcessingContinueException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
