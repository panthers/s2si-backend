package com.cp.s2si.service.processors.queue;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.cp.s2si.exception.MessageProcessingContinueException;
import com.cp.s2si.exception.MessageProcessingTerminateException;
import com.cp.s2si.exception.MessageValidationException;
import com.cp.s2si.jms.Listeners;
import com.cp.s2si.jms.tasks.AddQueueTopicTask;
import com.cp.s2si.jms.tasks.ByteArrayTaskBuilder;
import com.cp.s2si.service.processors.AbstractProcessor;
import com.cp.s2si.service.processors.ProcessorData;

/**
 * 
 * @author panther
 *
 */
@Component
@Scope("prototype")
public class Queue extends AbstractProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Queue.class);
	
	private String queuename;
	private UUID queueConsumerProcessorId;

	public Queue(Long scenarioId, UUID processorId, UUID chainProcessorId, UUID returnProcessorId,
			UUID nextProcessorId, Set<UUID> referredProcessorIds, String queuename, UUID queueConsumerProcessorId) {
		super(scenarioId, processorId, chainProcessorId, returnProcessorId, nextProcessorId, referredProcessorIds);
		this.queuename = queuename;
		this.queueConsumerProcessorId = queueConsumerProcessorId;
	}

	@Override
	public void validateMessage(ProcessorData message) throws MessageValidationException {
		// do nothing
	}

	@Override
	public ProcessorData process(ProcessorData message) throws MessageProcessingContinueException, MessageProcessingTerminateException {
		try {
			JmsTemplate jmsQueueTemplate = getBeanFromContext("jmsQueueTemplate", JmsTemplate.class);
			LOGGER.info("jmsQueueTemplate {}", jmsQueueTemplate);
			jmsQueueTemplate.convertAndSend(queuename, getBeanFromContext(ByteArrayTaskBuilder.class).wrap(message));
		} catch (IOException e) {
			LOGGER.error("Send to queue failed with exception ", e.getMessage());
			throw new MessageProcessingContinueException(e);
		}
		return message;
	}

	@Override
	public void initProcessor() throws Exception {
		//Setup queue and listener.
		AddQueueTopicTask addQueueTopicTask = new AddQueueTopicTask(queuename, queueConsumerProcessorId);
		getBeanFromContext("jmsTopicTemplate", JmsTemplate.class).convertAndSend(Listeners.S2SI_TOPIC, addQueueTopicTask);
	}
	
	@Override
	public void cleanUp() {
		// TODO think about clustered environment.
	}

}
