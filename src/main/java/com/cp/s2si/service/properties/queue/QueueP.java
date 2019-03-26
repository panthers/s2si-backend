package com.cp.s2si.service.properties.queue;

import java.util.UUID;

import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.queue.Queue;
import com.cp.s2si.service.properties.BaseP;

public class QueueP extends BaseP {
	
	private String queuename;
	private UUID queueConsumerProcessorId;

	@Override
	public Processor load() {
		return getApplicationContext().getBean(Queue.class, getScenarioId(), getProcessorId(), 
				getChainProcessorId(), getReturnProcessorId(), getNextProcessorId(), getReferredProcessorIds(), getQueuename(), getQueueConsumerProcessorId());
	}

	public String getQueuename() {
		return queuename;
	}

	public void setQueuename(String queuename) {
		this.queuename = queuename;
	}

	public UUID getQueueConsumerProcessorId() {
		return queueConsumerProcessorId;
	}

	public void setQueueConsumerProcessorId(UUID queueConsumerProcessorId) {
		this.queueConsumerProcessorId = queueConsumerProcessorId;
	}
	
}
