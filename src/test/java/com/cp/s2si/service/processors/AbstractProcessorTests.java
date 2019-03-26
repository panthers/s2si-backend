package com.cp.s2si.service.processors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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

import com.cp.s2si.exception.MessageValidationException;
import com.cp.s2si.service.processors.AbstractProcessor;
import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.ProcessorData;

/**
 * 
 * @author panther
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractProcessorTests {

	@Spy @InjectMocks
	private AbstractProcessor processor = mock(AbstractProcessor.class, Mockito.CALLS_REAL_METHODS);
	
	@Mock private ApplicationContext applicationContext;
	
	UUID mm1PUUID = UUID.randomUUID(); //Processor
	UUID mm1CUUID = UUID.randomUUID(); //Chain
	UUID mm1NUUID = UUID.randomUUID(); //Next
	UUID mm1RUUID = UUID.randomUUID(); //Return
	Set<UUID> mm1RPUUIDSet = new HashSet<UUID>(Arrays.asList(UUID.randomUUID())); //Referred Processor Set
	
	@Mock private Processor nextProcessor;
	@Mock private Processor chainProcessor;
	@Mock private Processor returnProcessor;
	
	@Before
	public void setUp() throws Exception {
		processor.setApplicationContext(applicationContext);
	}
	
	@Test(expected = MessageValidationException.class)
	public void runTestThrowsValidationException() throws Exception {
		// Mocking
		ProcessorData pd = mock(ProcessorData.class);
		doThrow(new MessageValidationException("")).when(processor).validateMessage(pd);
		
		// Executing
		processor.run(pd);
		
		// Verifying
	}
	
	@Test(expected = RuntimeException.class)
	public void runTestThrowsRuntimeException() throws Exception {
		// Mocking
		ProcessorData pd = mock(ProcessorData.class);
		doNothing().when(processor).validateMessage(pd);
		when(processor.process(pd)).thenThrow(mock(RuntimeException.class));
		
		// Executing
		processor.run(pd);
		
		// Verifying
		verify(processor, times(1)).validateMessage(pd);
	}
	
	@Test
	public void runTest() throws Exception {
		// Mocking
		ProcessorData pd_o = mock(ProcessorData.class);
		ProcessorData pd_p = mock(ProcessorData.class);
		ProcessorData pd_n = mock(ProcessorData.class);
		ProcessorData pd_r = mock(ProcessorData.class);
		ProcessorData pd_c = mock(ProcessorData.class);
		
		//Due to partial mocking we have to mock like this.
		doReturn(mm1PUUID).when(processor).getProcessorId();
		doReturn(mm1CUUID).when(processor).getChainProcessorId();
		doReturn(mm1NUUID).when(processor).getNextProcessorId();
		doReturn(mm1RUUID).when(processor).getReturnProcessorId();
		
		doNothing().when(processor).validateMessage(pd_o);
		doReturn(pd_p).when(processor).process(pd_o);
		doReturn(true).when(processor).validProcessor(mm1NUUID);
		doReturn(true).when(processor).validProcessor(mm1CUUID);
		doReturn(true).when(processor).validProcessor(mm1RUUID);
		doReturn(nextProcessor).when(processor).getProcessorFromUUID(mm1NUUID);
		doReturn(chainProcessor).when(processor).getProcessorFromUUID(mm1CUUID);
		doReturn(returnProcessor).when(processor).getProcessorFromUUID(mm1RUUID);
		doReturn(pd_n).when(nextProcessor).run(pd_p);
		doReturn(pd_r).when(returnProcessor).run(pd_n);
		doReturn(pd_c).when(chainProcessor).run(pd_o);
		
		// Executing
		ProcessorData pd_rr = processor.run(pd_o);
		
		// Verifying
		assertThat(pd_rr).isEqualTo(pd_r);
		verify(processor, atLeastOnce()).getProcessorId();
		verify(processor, atLeastOnce()).getChainProcessorId();
		verify(processor, atLeastOnce()).getNextProcessorId();
		verify(processor, atLeastOnce()).getReturnProcessorId();
		verify(processor, times(1)).validateMessage(pd_o);
		verify(processor, times(1)).process(pd_o);
		verify(nextProcessor, times(1)).run(pd_p);
		verify(returnProcessor, times(1)).run(pd_n);
		verify(chainProcessor, times(1)).run(pd_o);
		
	}
	
	
}
