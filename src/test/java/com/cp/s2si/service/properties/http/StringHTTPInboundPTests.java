package com.cp.s2si.service.properties.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.http.StringHTTPInbound;
import com.cp.s2si.service.properties.http.StringHTTPInboundP;

/**
 * 
 * @author panther
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class StringHTTPInboundPTests {
	
	@Spy @InjectMocks
	private StringHTTPInboundP property = new StringHTTPInboundP();
	
	@Test
	public void loadTest() {
		// Mocking
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		StringHTTPInbound processor = mock(StringHTTPInbound.class);
		UUID mm1PUUID = UUID.randomUUID(); //Processor
		UUID mm1CUUID = UUID.randomUUID(); //Chain
		UUID mm1NUUID = UUID.randomUUID(); //Next
		UUID mm1RUUID = UUID.randomUUID(); //Return
		Set<UUID> mm1RPUUIDSet = new HashSet<UUID>(Arrays.asList(UUID.randomUUID())); //Referred Processor Set
		RequestMethod[] rmA = { RequestMethod.POST };
		String[] mediaTypeA = { "application/json" };
		when(property.getApplicationContext()).thenReturn(applicationContext);
		when(property.getScenarioId()).thenReturn(0L);
		when(property.getProcessorId()).thenReturn(mm1PUUID);
		when(property.getChainProcessorId()).thenReturn(mm1CUUID);
		when(property.getNextProcessorId()).thenReturn(mm1NUUID);
		when(property.getReturnProcessorId()).thenReturn(mm1RUUID);
		when(property.getReferredProcessorIds()).thenReturn(mm1RPUUIDSet);
		when(property.getEndpoint()).thenReturn("END_POINT");
		when(property.getRequestMethods()).thenReturn(rmA);
		when(property.getMediaTypes()).thenReturn(mediaTypeA);
		when(applicationContext.getBean(StringHTTPInbound.class, 0L, mm1PUUID, 
				mm1CUUID, mm1RUUID, mm1NUUID, mm1RPUUIDSet, "END_POINT", rmA, mediaTypeA)).thenReturn(processor);
		
		// Executing
		Processor loadedProc = property.load();
		
		// Verifying
		assertThat(loadedProc).isEqualTo(processor);
		verify(property, times(1)).getApplicationContext();
		verify(property, times(1)).getScenarioId();
		verify(property, times(1)).getProcessorId();
		verify(property, times(1)).getChainProcessorId();
		verify(property, times(1)).getNextProcessorId();
		verify(property, times(1)).getReturnProcessorId();
		verify(property, times(1)).getReferredProcessorIds();
		verify(applicationContext, times(1)).getBean(StringHTTPInbound.class, 0L, mm1PUUID, 
				mm1CUUID, mm1RUUID, mm1NUUID, mm1RPUUIDSet, "END_POINT", rmA, mediaTypeA);
	}

}
