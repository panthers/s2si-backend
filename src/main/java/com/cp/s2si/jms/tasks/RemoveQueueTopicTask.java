package com.cp.s2si.jms.tasks;

/**
 * 
 * @author panther
 *
 */
public class RemoveQueueTopicTask implements ITopicTask {

	private static final long serialVersionUID = 6474554578589871196L;
	
	private String queueName;
	
	public RemoveQueueTopicTask(String queueName) {
		this.queueName = queueName;
	}

	public String getQueueName() {
		return queueName;
	}

}
