package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 * This exception is thrown by processors when they have failed and would like the process to terminate immediately.
 */
public class MessageProcessingTerminateException extends Exception {

	private static final long serialVersionUID = 1326301790710408585L;

	public MessageProcessingTerminateException() {
	}

	public MessageProcessingTerminateException(String message) {
		super(message);
	}

	public MessageProcessingTerminateException(Throwable cause) {
		super(cause);
	}

	public MessageProcessingTerminateException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageProcessingTerminateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
