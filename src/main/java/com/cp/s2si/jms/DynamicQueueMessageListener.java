package com.cp.s2si.jms;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import com.cp.s2si.exception.IllegalProcessorRetrievalException;
import com.cp.s2si.exception.MessageProcessingTerminateException;
import com.cp.s2si.exception.MessageValidationException;
import com.cp.s2si.jms.tasks.ByteArrayTaskBuilder;
import com.cp.s2si.jms.tasks.ByteArrayTaskBuilder.ByteArrayMessageTask;
import com.cp.s2si.service.ProcessorManagementService;

/**
 * Make a listener for each type of message.
 * 
 * @author panther
 *
 */
@Component
@Scope("prototype")
public class DynamicQueueMessageListener extends MessageListenerAdapter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicQueueMessageListener.class);
	
	private UUID queueConsumerProcessorId;
	
	@Autowired private ProcessorManagementService processorManagementService;
	@Autowired private ByteArrayTaskBuilder builder;
	
	public DynamicQueueMessageListener(UUID queueConsumerProcessorId) {
		super();
		setDefaultListenerMethod("messageHandler");
		this.queueConsumerProcessorId = queueConsumerProcessorId;
	}

	public void messageHandler(ByteArrayMessageTask task) {
		try {
			processorManagementService.getProcessorById(queueConsumerProcessorId).run(builder.unwrap(task));
		} catch (MessageValidationException | IllegalProcessorRetrievalException | MessageProcessingTerminateException e) {
			LOGGER.error("Message handling error on consumer processor {}", e.getMessage());
		}
	}

}
