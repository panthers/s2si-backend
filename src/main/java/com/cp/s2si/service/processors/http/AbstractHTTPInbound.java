package com.cp.s2si.service.processors.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.cp.s2si.exception.IllegalProcessorRetrievalException;
import com.cp.s2si.exception.MessageProcessingTerminateException;
import com.cp.s2si.exception.MessageValidationException;
import com.cp.s2si.filter.PreAuthenticated;
import com.cp.s2si.service.processors.AbstractProcessor;
import com.cp.s2si.service.processors.ProcessorData;

/**
 * 
 * @author panther
 *
 */
public abstract class AbstractHTTPInbound extends AbstractProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHTTPInbound.class);
	
	private String endpoint;
	private RequestMethod[] methods;
	private String[] mediaTypes;
	private RequestMappingInfo requestMappingInfo;
	
	public AbstractHTTPInbound(Long scenarioId, UUID processorId, 
			UUID chainProcessorId, UUID returnProcessorId, 
			UUID nextProcessorId, Set<UUID> referredProcessorIds, 
			String endpoint, RequestMethod[] requestMethods, String[] mediaTypes) {
		super(scenarioId, processorId, chainProcessorId, returnProcessorId, nextProcessorId, referredProcessorIds);
		this.endpoint = endpoint;
		this.mediaTypes = mediaTypes;
		this.methods = requestMethods;
	}
	
	public void handleRequests(HttpServletRequest request, HttpServletResponse response) {
		ProcessorData message;
		try {
			message = getMessageFromRequest(request, response);
			LOGGER.debug("Message from request for processor {} is {}", getProcessorId(), message);
		} catch (IOException e) {
			LOGGER.error("Get message failed for processor {} with exception {}", getProcessorId(), e);
			handleRequestForDataFailedHandler(request, response);
			return;
		}
		ProcessorData returnMessage;
		try {
			returnMessage = run(message);
			LOGGER.debug("Return message from run {}", returnMessage);
		} catch (MessageValidationException | IllegalProcessorRetrievalException | MessageProcessingTerminateException e) {
			LOGGER.error("Run failed for processor {} with exception {}", getProcessorId(), e);
			messageFailedHandler(e, request, response);
			return;
		}
		returnSuccessfullMessageHandler(returnMessage, response);
	}

	@Override
	public void initProcessor() throws Exception {
		
		Assert.notNull(endpoint, "Endpoint must be non-null");
		Assert.notNull(methods, "Methods array must be non-null");
		Assert.notNull(mediaTypes, "Mediatypes array must be non-null");
		
		this.requestMappingInfo = prepareRequestMappingInfo();
		
		getBeanFromContext(RequestMappingHandlerMapping.class).registerMapping(
	    		requestMappingInfo, 
	    		this,
	    		AbstractHTTPInbound.class.getMethod("handleRequests", HttpServletRequest.class, HttpServletResponse.class)
	    );
		
		getBeanFromContext(PreAuthenticated.class).exposeEndpoints(Arrays.asList(endpoint));
	}
	
	@Override
	public void cleanUp() {
		getBeanFromContext(PreAuthenticated.class).blockEndpoints(Arrays.asList(endpoint));
		getBeanFromContext(RequestMappingHandlerMapping.class).unregisterMapping(requestMappingInfo);
	}
	
	public void messageFailedHandler(Exception e, HttpServletRequest request, HttpServletResponse response) {
		try {
			response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
		} catch (IOException e1) {
			LOGGER.error("Failed to write error to response for processor {} with message {}", getProcessorId(), e.getMessage());
		}
	}
	
	public RequestMappingInfo prepareRequestMappingInfo() {
		return RequestMappingInfo
	            .paths(endpoint)
	            .methods(methods)
	            .produces(mediaTypes)
	            .build();
	}
	
	public abstract void returnSuccessfullMessageHandler(ProcessorData returnMessage, HttpServletResponse response);

	public abstract ProcessorData getMessageFromRequest(HttpServletRequest request, HttpServletResponse response) throws IOException;

	public abstract void handleRequestForDataFailedHandler(HttpServletRequest request, HttpServletResponse response);

}
