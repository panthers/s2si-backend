package com.cp.s2si.service.processors.string;

import java.io.IOException;
import java.util.Map;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * 
 * @author panther
 *
 */
@Component
@Scope("prototype")
public class XmlToJson extends AbstractProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XmlToJson.class);

	public XmlToJson(Long scenarioId, UUID processorId, UUID chainProcessorId, UUID returnProcessorId,
			UUID nextProcessorId, Set<UUID> referredProcessorIds) {
		super(scenarioId, processorId, chainProcessorId, returnProcessorId, nextProcessorId, referredProcessorIds);
	}

	@Override
	public void validateMessage(ProcessorData message) throws MessageValidationException {
		XmlMapper xm = new XmlMapper();
		if(message.isStringData()) {
			try {
				xm.readTree(message.getStringData());
			} catch (IOException e) {
				throw new MessageValidationException("String data is not a valid xml", e);
			}
		} else {
			throw new MessageValidationException("ProcessData is not a valid xml");
		}
	}

	@Override
	public ProcessorData process(ProcessorData message) throws MessageProcessingContinueException, MessageProcessingTerminateException {
		ObjectMapper om = new ObjectMapper();
		XmlMapper xm = new XmlMapper();
		String jsonString = "";
		try {
			jsonString = om.writeValueAsString(xm.readValue(message.getStringData(), new TypeReference<Map<String, Object>>(){}));
			ProcessorData pd = new ProcessorData();
			pd.setStringData(jsonString);
			pd.setPriority(message.getPriority());
			return pd;
		} catch (IOException e) {
			LOGGER.error("Error on process {}", e.getMessage());
			throw new MessageProcessingContinueException(e);
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
