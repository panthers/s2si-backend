package com.cp.s2si.service.processors.http;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cp.s2si.exception.MessageProcessingContinueException;
import com.cp.s2si.exception.MessageProcessingTerminateException;
import com.cp.s2si.exception.MessageValidationException;
import com.cp.s2si.service.processors.ProcessorData;

/**
 * 
 * @author panther
 *
 */
@Component
@Scope("prototype")
public class StringHTTPInbound extends AbstractHTTPInbound {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StringHTTPInbound.class);
	
	public StringHTTPInbound(Long scenarioId, UUID processorId, UUID chainProcessorId, UUID returnProcessorId,
			UUID nextProcessorId, Set<UUID> referredProcessorIds, String endpoint, RequestMethod[] requestMethods,
			String[] mediaTypes) {
		super(scenarioId, processorId, chainProcessorId, returnProcessorId, nextProcessorId, referredProcessorIds, endpoint,
				requestMethods, mediaTypes);
	}

	@Override
	public void returnSuccessfullMessageHandler(ProcessorData returnMessage, HttpServletResponse response) {
		try {
			if(returnMessage != null) {
				response.getWriter().write(returnMessage.getStringData());
				LOGGER.debug("Return message written to response for processor ? is ?", getProcessorId(), returnMessage);
			}
			response.setStatus(HttpStatus.OK.value());
			LOGGER.debug("Response code set with 200 for processor ?", getProcessorId());
		} catch (IOException e) {
			LOGGER.error("Response writing failed for processor ? with return message ?", getProcessorId(), returnMessage);
			response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
			LOGGER.error("Response code set with 417 for processore ?", getProcessorId());
		}
	}

	@Override
	public void validateMessage(ProcessorData message) throws MessageValidationException {
		if(message == null) {
			throw new MessageValidationException("Message is null or empty");
		}
	}

	@Override
	public ProcessorData getMessageFromRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ProcessorData pd = new ProcessorData();
		pd.setStringData(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
		return pd;
	}

	@Override
	public void handleRequestForDataFailedHandler(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpStatus.BAD_REQUEST.value());
	}

	@Override
	public void initProcessor() throws Exception {
		super.initProcessor();
	}

	@Override
	public ProcessorData process(ProcessorData message) throws MessageProcessingContinueException, MessageProcessingTerminateException {
		return message;
	}

}
