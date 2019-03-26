package com.cp.s2si.service.processors.string;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.cp.s2si.service.processors.ProcessorData;
import com.cp.s2si.service.processors.string.StringPrint;

/**
 * 
 * @author panther
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class StringPrintTests {
	
	UUID mm1PUUID = UUID.randomUUID(); //Processor
	UUID mm1CUUID = UUID.randomUUID(); //Chain
	UUID mm1NUUID = UUID.randomUUID(); //Next
	UUID mm1RUUID = UUID.randomUUID(); //Return
	Set<UUID> mm1RPUUIDSet = new HashSet<UUID>(Arrays.asList(UUID.randomUUID())); //Referred Processor Set
	
	@Spy @InjectMocks
	private StringPrint processor = new StringPrint(0L, mm1PUUID, mm1CUUID, mm1RUUID, mm1NUUID, mm1RPUUIDSet);
	
	@Test
	public void initProcessorTest() throws Exception {
		// Mocking
		
		// Executing
		processor.initProcessor();
		
		// Verifying
		verify(processor, times(1)).initProcessor();
		verifyNoMoreInteractions(processor);
	}
	
	@Test
	public void validateMessageTest() throws Exception {
		// Mocking
		
		// Executing
		processor.validateMessage(mock(ProcessorData.class));
		
		// Verifying
		verify(processor, times(1)).validateMessage(any(ProcessorData.class));
		verifyNoMoreInteractions(processor);
	}
	
	@Test
	public void cleanTest() throws Exception {
		// Mocking
		
		// Executing
		processor.cleanUp();
		
		// Verifying
		verify(processor, times(1)).cleanUp();
		verifyNoMoreInteractions(processor);
	}
	
	@Test
	public void processTest() throws Exception {
		// Mocking
		ProcessorData pd = mock(ProcessorData.class);
		when(processor.getProcessorId()).thenReturn(mm1PUUID);
		
		// Executing
		ProcessorData processedPD = processor.process(pd);
		
		// Verifying
		assertThat(processedPD).isEqualTo(pd);
		verify(processor, times(1)).getProcessorId();
		verify(processor, times(1)).process(pd);
		verifyNoMoreInteractions(processor);
	}

}
