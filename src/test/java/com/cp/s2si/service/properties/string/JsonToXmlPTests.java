package com.cp.s2si.service.properties.string;

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

import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.string.JsonToXml;
import com.cp.s2si.service.properties.string.JsonToXmlP;

/**
 * 
 * @author panther
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonToXmlPTests {
	
	@Spy @InjectMocks
	private JsonToXmlP property = new JsonToXmlP();
	
	@Test
	public void loadTest() {
		// Mocking
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		JsonToXml processor = mock(JsonToXml.class);
		UUID mm1PUUID = UUID.randomUUID(); //Processor
		UUID mm1CUUID = UUID.randomUUID(); //Chain
		UUID mm1NUUID = UUID.randomUUID(); //Next
		UUID mm1RUUID = UUID.randomUUID(); //Return
		Set<UUID> mm1RPUUIDSet = new HashSet<UUID>(Arrays.asList(UUID.randomUUID())); //Referred Processor Set
		when(property.getApplicationContext()).thenReturn(applicationContext);
		when(property.getScenarioId()).thenReturn(0L);
		when(property.getProcessorId()).thenReturn(mm1PUUID);
		when(property.getChainProcessorId()).thenReturn(mm1CUUID);
		when(property.getNextProcessorId()).thenReturn(mm1NUUID);
		when(property.getReturnProcessorId()).thenReturn(mm1RUUID);
		when(property.getReferredProcessorIds()).thenReturn(mm1RPUUIDSet);
		when(applicationContext.getBean(JsonToXml.class, 0L, mm1PUUID, 
				mm1CUUID, mm1RUUID, mm1NUUID, mm1RPUUIDSet)).thenReturn(processor);
		
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
		verify(applicationContext, times(1)).getBean(JsonToXml.class, 0L, mm1PUUID, 
				mm1CUUID, mm1RUUID, mm1NUUID, mm1RPUUIDSet);
	}

}
