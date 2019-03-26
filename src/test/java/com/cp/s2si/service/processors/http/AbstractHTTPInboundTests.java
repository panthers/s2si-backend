package com.cp.s2si.service.processors.http;

import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.cp.s2si.Whitebox;
import com.cp.s2si.filter.PreAuthenticated;
import com.cp.s2si.service.processors.http.AbstractHTTPInbound;

/**
 * 
 * @author panther
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractHTTPInboundTests {
	
	UUID mm1PUUID = UUID.randomUUID(); //Processor
	UUID mm1CUUID = UUID.randomUUID(); //Chain
	UUID mm1NUUID = UUID.randomUUID(); //Next
	UUID mm1RUUID = UUID.randomUUID(); //Return
	Set<UUID> mm1RPUUIDSet = new HashSet<UUID>(Arrays.asList(UUID.randomUUID())); //Referred Processor Set
	
	@Spy @InjectMocks
	private AbstractHTTPInbound processor = mock(AbstractHTTPInbound.class, Mockito.CALLS_REAL_METHODS);
	
	@Mock private ApplicationContext applicationContext;
	
	@Before
	public void setUp() throws Exception {
		processor.setApplicationContext(applicationContext);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void initProcessorTest1() throws Exception {
		// Mocking
		//Whitebox.setInternalState(processor, "endpoint", "string_http_inbound");
		//Whitebox.setInternalState(processor, "methods", new RequestMethod[] {RequestMethod.GET, RequestMethod.POST});
		//Whitebox.setInternalState(processor, "mediaTypes", new String[] {"application/json"});
		
		// Executing
		processor.initProcessor();
		
		// Verifying
		// nothing
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void initProcessorTest2() throws Exception {
		// Mocking
		Whitebox.setInternalState(processor, "endpoint", "string_http_inbound");
		//Whitebox.setInternalState(processor, "methods", new RequestMethod[] {RequestMethod.GET, RequestMethod.POST});
		//Whitebox.setInternalState(processor, "mediaTypes", new String[] {"application/json"});
		
		// Executing
		processor.initProcessor();
		
		// Verifying
		// nothing
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void initProcessorTest3() throws Exception {
		// Mocking
		Whitebox.setInternalState(processor, "endpoint", "string_http_inbound");
		Whitebox.setInternalState(processor, "methods", new RequestMethod[] {RequestMethod.GET, RequestMethod.POST});
		//Whitebox.setInternalState(processor, "mediaTypes", new String[] {"application/json"});
		
		// Executing
		processor.initProcessor();
		
		// Verifying
		// nothing
	}
	
	@Test
	public void initProcessorTest4() throws Exception {
		// Mocking
		Whitebox.setInternalState(processor, "endpoint", "string_http_inbound");
		Whitebox.setInternalState(processor, "methods", new RequestMethod[] {RequestMethod.GET, RequestMethod.POST});
		Whitebox.setInternalState(processor, "mediaTypes", new String[] {"application/json"});
		RequestMappingHandlerMapping rmhm = mock(RequestMappingHandlerMapping.class);
		doReturn(rmhm).when(processor).getBeanFromContext(eq(RequestMappingHandlerMapping.class));
		doNothing().when(rmhm).registerMapping(any(RequestMappingInfo.class), eq(processor), any(Method.class));
		PreAuthenticated pa = mock(PreAuthenticated.class);
		doReturn(pa).when(processor).getBeanFromContext(eq(PreAuthenticated.class));
		doNothing().when(pa).exposeEndpoints(anyList());
		
		// Executing
		processor.initProcessor();
		
		// Verifying
		verify(processor, times(1)).getBeanFromContext(eq(RequestMappingHandlerMapping.class));
		verify(rmhm, times(1)).registerMapping(any(RequestMappingInfo.class), eq(processor), any(Method.class));
		verify(processor, times(1)).getBeanFromContext(eq(PreAuthenticated.class));
		verify(pa, times(1)).exposeEndpoints(anyList());
	}

}
