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
import com.cp.s2si.service.processors.string.XPathToBoolean;
import com.cp.s2si.service.properties.string.XPathToBooleanP;

/**
 * 
 * @author panther
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class XPathToBooleanPTests {
	
	@Spy @InjectMocks
	private XPathToBooleanP property = new XPathToBooleanP();
	
	@Test
	public void loadTest() {
		// Mocking
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		XPathToBoolean processor = mock(XPathToBoolean.class);
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
		when(property.getXpathTrue()).thenReturn("XPATH_EXPR_TRUE");
		when(property.getXpathFalse()).thenReturn("XPATH_EXPR_FALSE");
		when(property.isDefaultBool()).thenReturn(false);
		when(applicationContext.getBean(XPathToBoolean.class, 0L, mm1PUUID, 
				mm1CUUID, mm1RUUID, mm1NUUID, mm1RPUUIDSet, "XPATH_EXPR_TRUE", "XPATH_EXPR_FALSE", false)).thenReturn(processor);
		
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
		verify(applicationContext, times(1)).getBean(XPathToBoolean.class, 0L, mm1PUUID, 
				mm1CUUID, mm1RUUID, mm1NUUID, mm1RPUUIDSet, "XPATH_EXPR_TRUE", "XPATH_EXPR_FALSE", false);
	}

}
