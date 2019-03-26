package com.cp.s2si.jms.tasks;

import java.util.UUID;

/**
 * 
 * @author panther
 *
 */
public class AddQueueTopicTask implements ITopicTask {

	private static final long serialVersionUID = -1886529321425108053L;
	
	private String queueName;
	private UUID queueConsumerProcessorId;
	
	public AddQueueTopicTask(String queueName, UUID queueConsumerProcessorId) {
		this.queueName = queueName;
		this.queueConsumerProcessorId = queueConsumerProcessorId;
	}

	public String getQueueName() {
		return queueName;
	}
	
	public UUID getQueueConsumerProcessorId() {
		return queueConsumerProcessorId;
	}
	
}
