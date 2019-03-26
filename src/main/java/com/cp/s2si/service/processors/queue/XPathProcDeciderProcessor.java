package com.cp.s2si.service.processors.queue;

import java.util.Set;
import java.util.UUID;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.cp.s2si.exception.IllegalProcessorRetrievalException;
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
public class XPathProcDeciderProcessor extends AbstractProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XPathProcDeciderProcessor.class);
	
	private String[] xpath_expressions;
	private UUID[] processorIds;

	public XPathProcDeciderProcessor(Long scenarioId, UUID processorId, UUID chainProcessorId,
			UUID returnProcessorId, UUID nextProcessorId, Set<UUID> referredProcessorIds,
			String[] xpath_expressions, UUID[] processorIds) {
		super(scenarioId, processorId, chainProcessorId, returnProcessorId, nextProcessorId, referredProcessorIds);
		this.xpath_expressions = xpath_expressions;
		this.processorIds = processorIds;
	}

	private UUID decideProcessor(ProcessorData message) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		for(int i = 0; i < xpath_expressions.length; i++) {
			try {
				Boolean isMatching = (Boolean) xPath.compile(xpath_expressions[i]).evaluate(message, XPathConstants.BOOLEAN);
				if(isMatching) {
					return processorIds[i];
				}
			} catch (XPathExpressionException e) {
				//TODO 
			}
		}
		return null;
	}

	@Override
	public void validateMessage(ProcessorData message) throws MessageValidationException {
		// do nothing
	}

	@Override
	public ProcessorData process(ProcessorData message) throws MessageProcessingContinueException, MessageProcessingTerminateException {
		try {
			getProcessorFromUUID(decideProcessor(message)).run(message);
		} catch (MessageValidationException | IllegalProcessorRetrievalException e) {
			LOGGER.error("Proc decider exception {}", e.getMessage());
		}
		return message;
	}

	@Override
	public void initProcessor() throws Exception {
		//Throw error if length doesnot match
		Assert.isTrue(xpath_expressions.length == processorIds.length, "Xpath expressions and processor ids count must match.");
	}

	@Override
	public void cleanUp() {
		// do nothing
	}

}
