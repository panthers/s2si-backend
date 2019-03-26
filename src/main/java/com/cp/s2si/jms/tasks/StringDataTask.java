package com.cp.s2si.jms.tasks;

/**
 * 
 * @author panther
 *
 */
public class StringDataTask extends AbstractMessageOnQueueTask<String> {

	private static final long serialVersionUID = 4310358375995197638L;
	
	private String message;

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
