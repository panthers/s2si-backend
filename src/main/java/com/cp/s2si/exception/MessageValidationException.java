package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 */
public class MessageValidationException extends Exception {

	private static final long serialVersionUID = -5502292285725407297L;
	
	public MessageValidationException(String message) {
		super(message);
	}

	public MessageValidationException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
