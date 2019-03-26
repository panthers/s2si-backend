package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 */
public class FileNotReadyException extends Exception {

	private static final long serialVersionUID = -3539661660223931817L;

	public FileNotReadyException() {
		super();
	}

	public FileNotReadyException(String message) {
		super(message);
	}

	public FileNotReadyException(Throwable cause) {
		super(cause);
	}

}
