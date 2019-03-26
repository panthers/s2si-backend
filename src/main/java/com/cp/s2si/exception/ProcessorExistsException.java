package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 */
public class ProcessorExistsException extends Exception {

	private static final long serialVersionUID = 4955948504740769251L;

	public ProcessorExistsException() {
		super();
	}

	public ProcessorExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProcessorExistsException(String message) {
		super(message);
	}

	public ProcessorExistsException(Throwable cause) {
		super(cause);
	}
	
}
