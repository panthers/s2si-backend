package com.cp.s2si.service;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import com.cp.s2si.persistence.ProcessorRepository;
import com.cp.s2si.persistence.models.ProcessorMainModel;
import com.cp.s2si.persistence.models.ProcessorRefModel;
import com.cp.s2si.persistence.models.ProcessorRefPK;
import com.cp.s2si.service.ProcessorManagementServiceImpl;
import com.cp.s2si.service.processors.string.StringPrint;
import com.cp.s2si.service.properties.string.StringPrintP;

/**
 * 
 * @author panther
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ProcessorManagementServiceImplTests {
	
	@Mock private ApplicationContext applicationContext;
	@Mock private ProcessorRepository processorRepository;
	
	@Spy @InjectMocks
	private ProcessorManagementServiceImpl processorManagementService = new ProcessorManagementServiceImpl();
	
	@Test
	public void startTest() {
		// Mocking
		ProcessorMainModel mm1 = new ProcessorMainModel();
		mm1.setScenarioId(0L);
		UUID mm1PUUID = UUID.randomUUID(); //Processor
		UUID mm1CUUID = UUID.randomUUID(); //Chain
		UUID mm1NUUID = UUID.randomUUID(); //Next
		UUID mm1RUUID = UUID.randomUUID(); //Return
		UUID mm1RPUUID = UUID.randomUUID(); //Referred Processor
		ProcessorRefModel refM = new ProcessorRefModel();
		ProcessorRefPK refPK = new ProcessorRefPK();
		refPK.setProcessorId(mm1PUUID);
		refPK.setReferredProcessorId(mm1RPUUID);
		refM.setRefPK(refPK);
		mm1.setReferredProcs(Arrays.asList(refM));
		mm1.setProcessorId(mm1PUUID);
		mm1.setChainProcessorId(mm1CUUID);
		mm1.setNextProcessorId(mm1NUUID);
		mm1.setReturnProcessorId(mm1RUUID);
		mm1.setPropertyClass(StringPrintP.class.getName());
		mm1.setPropertyJson("{}");
		mm1.setActive(true);
		ProcessorMainModel mm2 = new ProcessorMainModel();
		mm2.setActive(true);
		mm2.setPropertyClass("No Such Class");
		ProcessorMainModel mm3 = new ProcessorMainModel();
		mm3.setActive(false);
		mm3.setPropertyClass("No Such Class");
		ProcessorMainModel m1 = Mockito.spy(mm1);
		ProcessorMainModel m2 = Mockito.spy(mm2);
		ProcessorMainModel m3 = Mockito.spy(mm3);
		when(processorRepository.findAll()).thenReturn(Arrays.asList(m1, m2, m3));
		when(applicationContext.getBean(eq(StringPrint.class), eq(0L), eq(mm1PUUID), 
				eq(mm1CUUID), eq(mm1RUUID), eq(mm1NUUID), any(Set.class))).thenReturn(Mockito.mock(StringPrint.class));
		ContextRefreshedEvent cre = Mockito.mock(ContextRefreshedEvent.class);
		
		// Executing
		processorManagementService.start(cre); //1st Call
		processorManagementService.start(cre); //2nd Call should skip completely.
		
		// Verifying
		verify(processorRepository, times(1)).findAll();
		verify(applicationContext, times(1)).getBean(eq(StringPrint.class), eq(0L), eq(mm1PUUID), 
				eq(mm1CUUID), eq(mm1RUUID), eq(mm1NUUID), any(Set.class));
		verify(m1, times(1)).isActive();
		verify(m1, times(1)).getPropertyClass();
		verify(m1, times(1)).getPropertyJson();
		verify(m1, times(2)).getProcessorId();
		verify(m1, times(1)).getChainProcessorId();
		verify(m1, times(1)).getNextProcessorId();
		verify(m1, times(1)).getReturnProcessorId();
		verify(m1, times(1)).getReferredProcs();
		verify(m2, times(1)).isActive();
		verify(m2, times(1)).getPropertyClass();
		verify(m2, times(0)).getPropertyJson();
		verify(m2, times(0)).getProcessorId();
		verify(m2, times(0)).getChainProcessorId();
		verify(m2, times(0)).getNextProcessorId();
		verify(m2, times(0)).getReturnProcessorId();
		verify(m2, times(0)).getReferredProcs();
	}

}
