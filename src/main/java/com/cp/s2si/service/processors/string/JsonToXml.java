package com.cp.s2si.service.processors.string;

import java.io.IOException;
import java.util.LinkedHashMap;
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
public class JsonToXml extends AbstractProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonToXml.class);

	public JsonToXml(Long scenarioId, UUID processorId, UUID chainProcessorId, UUID returnProcessorId,
			UUID nextProcessorId, Set<UUID> referredProcessorIds) {
		super(scenarioId, processorId, chainProcessorId, returnProcessorId, nextProcessorId, referredProcessorIds);
	}

	@Override
	public void validateMessage(ProcessorData message) throws MessageValidationException {
		ObjectMapper om = new ObjectMapper();
		if(message.isStringData()) {
			try {
				om.readTree(message.getStringData());
			} catch (IOException e) {
				throw new MessageValidationException("String data is not a valid json", e);
			}
		} else {
			throw new MessageValidationException("ProcessData is not a valid json");
		}
	}

	@Override
	public ProcessorData process(ProcessorData message) throws MessageProcessingContinueException, MessageProcessingTerminateException {
		ObjectMapper om = new ObjectMapper();
		XmlMapper xm = new XmlMapper();
		String xmlString = "";
		if(message.isStringData()) {
			try {
				xmlString = xm.writeValueAsString(om.readValue(message.getStringData(), new TypeReference<LinkedHashMap<String, Object>>(){}));
				xmlString = xmlString.substring(15, xmlString.length() - 16); // Remove the LinkedHashMap root tag
				message.setStringData(xmlString);
			} catch (IOException e) {
				LOGGER.error("String data error {}", e.getMessage());
				throw new MessageProcessingContinueException(e);
			}
		}
		return message;
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
