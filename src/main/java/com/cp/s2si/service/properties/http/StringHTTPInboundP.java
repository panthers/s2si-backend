package com.cp.s2si.service.properties.http;

import org.springframework.web.bind.annotation.RequestMethod;

import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.http.StringHTTPInbound;
import com.cp.s2si.service.properties.BaseP;

public class StringHTTPInboundP extends BaseP {
	
	private String endpoint;
	private RequestMethod[] requestMethods;
	private String[] mediaTypes;

	@Override
	public Processor load() {
		return getApplicationContext().getBean(StringHTTPInbound.class, getScenarioId(), getProcessorId(), 
				getChainProcessorId(), getReturnProcessorId(), getNextProcessorId(), getReferredProcessorIds(),
				getEndpoint(), getRequestMethods(), getMediaTypes());
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public RequestMethod[] getRequestMethods() {
		return requestMethods;
	}

	public void setRequestMethods(RequestMethod[] requestMethods) {
		this.requestMethods = requestMethods;
	}

	public String[] getMediaTypes() {
		return mediaTypes;
	}

	public void setMediaTypes(String[] mediaTypes) {
		this.mediaTypes = mediaTypes;
	}
	
//	public RequestMethod[] getRequestMethods() {
//		return Arrays.stream(requestMethods)
//				.map(method -> RequestMethod.valueOf(method))
//				.collect(Collectors.toList())
//				.toArray(new RequestMethod[requestMethods.length]);
//	}
//
//	public void setRequestMethods(RequestMethod... requestMethods) {
//		this.requestMethods = Arrays.stream(requestMethods)
//				.map(method -> method.name())
//				.collect(Collectors.toList())
//				.toArray(new String[requestMethods.length]);
//	}
	
//	public static void main(String[] args) throws Exception {
//		StringHTTPInboundP p = new StringHTTPInboundP();
//		p.setEndpoint("abcd");
//		p.setRequestMethods(new RequestMethod[] {RequestMethod.GET, RequestMethod.POST});
//		p.setMediaTypes(new String[] { "application/json" });
//		StringWriter sw = new StringWriter();
//		new ObjectMapper().writeValue(sw, p);
//		System.out.println(sw);
//		StringHTTPInboundP p1 = new ObjectMapper().readValue(sw.toString(), StringHTTPInboundP.class);
//		System.out.println(p1);
//	}
}
