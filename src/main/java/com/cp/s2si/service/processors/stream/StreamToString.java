package com.cp.s2si.service.processors.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

/**
 * 
 * @author panther
 *
 */
@Component
@Scope("prototype")
public class StreamToString extends AbstractProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StreamToString.class);

	public StreamToString(Long scenarioId, UUID processorId, UUID chainProcessorId, UUID returnProcessorId,
			UUID nextProcessorId, Set<UUID> referredProcessorIds) {
		super(scenarioId, processorId, chainProcessorId, returnProcessorId, nextProcessorId, referredProcessorIds);
	}

	@Override
	public void validateMessage(ProcessorData message) throws MessageValidationException {
		if(!message.isStreamData()) {
			throw new MessageValidationException("ProcessData is not a valid stream");
		}
	}

	@Override
	public ProcessorData process(ProcessorData message) throws MessageProcessingContinueException, MessageProcessingTerminateException {
		try {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = message.getStream().read(buffer)) != -1) {
			    result.write(buffer, 0, length);
			}
			ProcessorData pd = new ProcessorData();
			pd.setPriority(message.getPriority());
			pd.setStringData(result.toString(StandardCharsets.UTF_8.name()));
			return pd;
		} catch (IOException e) {
			LOGGER.error("String data error {}", e.getMessage());
			throw new MessageProcessingTerminateException(e);
		}
	}

	@Override
	public void initProcessor() throws Exception {
		// do nothing
	}

	@Override
	public void cleanUp() {
		// do nothing
	}

}
