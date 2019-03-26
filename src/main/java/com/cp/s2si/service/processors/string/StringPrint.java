package com.cp.s2si.service.processors.string;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cp.s2si.exception.MessageProcessingContinueException;
import com.cp.s2si.exception.MessageProcessingTerminateException;
import com.cp.s2si.exception.MessageValidationException;
import com.cp.s2si.service.processors.AbstractProcessor;
import com.cp.s2si.service.processors.ProcessorData;

@Component
@Scope("prototype")
public class StringPrint extends AbstractProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StringPrint.class);
	
	public StringPrint(Long scenarioId, UUID processorId, UUID chainProcessorId, UUID returnProcessorId,
			UUID nextProcessorId, Set<UUID> referredProcessorIds) {
		super(scenarioId, processorId, chainProcessorId, returnProcessorId, nextProcessorId, referredProcessorIds);
	}

	@Override
	public void initProcessor() throws Exception {
		// do nothing
	}

	@Override
	public void validateMessage(ProcessorData message) throws MessageValidationException {
		// do nothing
	}

	@Override
	public ProcessorData process(ProcessorData message) throws MessageProcessingContinueException, MessageProcessingTerminateException {
		LOGGER.info("Printing message for processor {}. \nMessage is {}", getProcessorId(), message);
		return message;
	}
	
	@Override
	public void cleanUp() {
		// do nothing
	}

}
