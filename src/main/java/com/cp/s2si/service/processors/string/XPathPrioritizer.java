package com.cp.s2si.service.processors.string;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.cp.s2si.exception.MessageProcessingContinueException;
import com.cp.s2si.exception.MessageProcessingTerminateException;
import com.cp.s2si.exception.MessageValidationException;
import com.cp.s2si.jms.tasks.Priority;
import com.cp.s2si.service.processors.AbstractProcessor;
import com.cp.s2si.service.processors.ProcessorData;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * 
 * @author panther
 *
 */
@Component
@Scope("prototype")
public class XPathPrioritizer extends AbstractProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XPathPrioritizer.class);
	
	private String xpathHigh;
	private String xpathLow;
	
	private boolean executeExprHigh = false;
	private boolean executeExprLow = false;
	
	public XPathPrioritizer(Long scenarioId, UUID processorId, UUID chainProcessorId,
			UUID returnProcessorId, UUID nextProcessorId, Set<UUID> referredProcessorIds,
			String xpathHigh, String xpathLow) {
		super(scenarioId, processorId, chainProcessorId, returnProcessorId, nextProcessorId, referredProcessorIds);
		this.xpathHigh = xpathHigh;
		this.xpathLow = xpathLow;
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
		XPath xPath = XPathFactory.newInstance().newXPath();
		InputStream mis;
		if(message.isStringData()) {
			mis = new ByteArrayInputStream(message.getStringData().getBytes());
		} else {
			throw new MessageProcessingContinueException("no string data on xpath prioritizer");
		}
		try {
			Priority priority = message.getPriority(); 
			Element node = DocumentBuilderFactory
				    .newInstance()
				    .newDocumentBuilder()
				    .parse(mis)
				    .getDocumentElement();
			if(executeExprLow) {
				Boolean result = (Boolean) xPath.compile(xpathLow).evaluate(node, XPathConstants.BOOLEAN);
				if(result) {
					priority = Priority.LOW;
				}
			}
			if(executeExprHigh) {
				Boolean result = (Boolean) xPath.compile(xpathHigh).evaluate(node, XPathConstants.BOOLEAN);
				if(result) {
					priority = Priority.HIGH;
				}
			}
			message.setPriority(priority);
			return message;
		} catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
			LOGGER.error("processor {} with XPATH Expression exception {}", getProcessorId(), e);
			throw new MessageProcessingContinueException(e);
		}
	}
	
	@Override
	public void initProcessor() throws Exception {
		// will compile and check if expressions are correct, else will throw exception.
		if(!StringUtils.isEmpty(xpathHigh)) {
			XPathFactory.newInstance().newXPath().compile(xpathHigh);
			executeExprHigh = true;
		}
		if(!StringUtils.isEmpty(xpathLow)) {
			XPathFactory.newInstance().newXPath().compile(xpathLow);
			executeExprLow = true;
		}
	}
	
	@Override
	public void cleanUp() {
		// do nothing
	}
	
}
