package com.cp.s2si.exception;

import java.time.LocalDateTime;

/**
 * 
 * @author panther
 *
 */
public class JWTExpiredException extends Exception {

	private static final long serialVersionUID = 5372049449306522122L;
	
	private LocalDateTime expiredTime = null;
	
	private String subject = null;

	public JWTExpiredException() {
	}

	public JWTExpiredException(String message) {
		super(message);
	}
	
	public JWTExpiredException(String message, String subject, LocalDateTime expiredTime) {
		super(message);
		this.subject = subject;
		this.expiredTime = expiredTime;
	}

	public JWTExpiredException(Throwable cause) {
		super(cause);
	}

	public JWTExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

	public JWTExpiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public String getSubject() {
		return subject;
	}

	public LocalDateTime getExpiredTime() {
		return expiredTime;
	}
	
}
