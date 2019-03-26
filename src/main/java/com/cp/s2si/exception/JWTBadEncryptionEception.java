package com.cp.s2si.exception;

/**
 * 
 * @author panther
 *
 */
public class JWTBadEncryptionEception extends Exception {

	private static final long serialVersionUID = 3221308164571151980L;

	public JWTBadEncryptionEception() {
	}

	public JWTBadEncryptionEception(String message) {
		super(message);
	}

	public JWTBadEncryptionEception(Throwable cause) {
		super(cause);
	}

	public JWTBadEncryptionEception(String message, Throwable cause) {
		super(message, cause);
	}

	public JWTBadEncryptionEception(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
