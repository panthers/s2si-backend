package com.cp.s2si.service.processors;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.cp.s2si.exception.IllegalProcessorRetrievalException;
import com.cp.s2si.exception.MessageProcessingContinueException;
import com.cp.s2si.exception.MessageProcessingTerminateException;
import com.cp.s2si.exception.MessageValidationException;
import com.cp.s2si.exception.ProcessorExistsException;
import com.cp.s2si.service.ProcessorManagementService;

/**
 * 
 * @author panther
 *
 */
public abstract class AbstractProcessor implements Processor, InitializingBean, ApplicationContextAware {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProcessor.class);
			
	private ApplicationContext applicationContext;
	private Long scenarioId;
	private UUID processorId;
	private UUID chainProcessorId;
	private UUID returnProcessorId;
	private UUID nextProcessorId;
	private Set<UUID> referredProcessorIds;
	
	public AbstractProcessor(Long scenarioId, UUID processorId, UUID chainProcessorId, UUID returnProcessorId, UUID nextProcessorId, Set<UUID> referredProcessorIds) {
		this.scenarioId = scenarioId;
		this.processorId = processorId;
		this.chainProcessorId = chainProcessorId;
		this.returnProcessorId = returnProcessorId;
		this.nextProcessorId = nextProcessorId;
		this.referredProcessorIds = referredProcessorIds;
	}
	
	public static final void unregister(UUID uuid) {
//		if(PROCESSOR_MAP.containsKey(uuid)) {
//			PROCESSOR_MAP.remove(uuid).close();
//		}
	}

	@Override
	public Long getScenarioId() {
		return this.scenarioId;
	}

	@Override
	public UUID getProcessorId() {
		return this.processorId;
	}
	
	@Override
	public UUID getChainProcessorId() {
		return this.chainProcessorId;
	}

	@Override
	public UUID getReturnProcessorId() {
		return this.returnProcessorId;
	}

	@Override
	public UUID getNextProcessorId() {
		return this.nextProcessorId;
	}

	@Override
	public Set<UUID> getReferredProcessorIds() {
		return this.referredProcessorIds;
	}

	public <C> C getBeanFromContext(Class<C> c, Object... args) {
		return applicationContext.getBean(c, args);
	}
	
	public <C> C getBeanFromContext(String beanname, Class<C> c) {
		return applicationContext.getBean(beanname, c);
	}
	
	@Override
	public final ProcessorData run(ProcessorData original) throws MessageValidationException, IllegalProcessorRetrievalException, MessageProcessingTerminateException {
		
		// Validate message
		LOGGER.debug("Validating message for processor {}", getProcessorId());
		validateMessage(original);
		LOGGER.debug("Message validated");
		
		// Run HTTP internal
		LOGGER.debug("Processor {}", getProcessorId());
		boolean processedSuccessfully = false;
		ProcessorData processed = null;
		try {
			processed = process(original);
			LOGGER.debug("Process output {}", processed);
			
			if(validProcessor(getNextProcessorId())) {
				LOGGER.debug("Next scenario {} processor {} with next processor {}", getScenarioId(), getProcessorId(), getNextProcessorId());
				processed = getProcessorFromUUID(getNextProcessorId()).run(processed);
				LOGGER.debug("Next output {}", processed);
			}
			
			if(validProcessor(getReturnProcessorId())) {
				LOGGER.debug("Return scenario {} processor {} with return processor {}", getScenarioId(), getProcessorId(), getReturnProcessorId());
				processed = getProcessorFromUUID(getReturnProcessorId()).run(processed);
				LOGGER.debug("Return output {}", processed);
			}
			processedSuccessfully = true;
		} catch (MessageProcessingContinueException e) {
			LOGGER.error("MessageProcessingContinueException caught on processor {} with error {}", getProcessorId(), e.getMessage());
		}
		//Chain to chain processor
		if(validProcessor(getChainProcessorId())) {
			LOGGER.debug("Chaining scenario {} processor {} with chain processor {}", getScenarioId(), getProcessorId(), getChainProcessorId());
			Object o = getProcessorFromUUID(getChainProcessorId()).run(original);
			LOGGER.debug("Chain output {}", o);
		}
		LOGGER.info("Returning message from run of processor {} after processedSuccessfully {}", getProcessorId(), processedSuccessfully);
		return processedSuccessfully ? processed : original;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public final void close() {
//		if(chainProcessor != null) {
//			try {
//				chainProcessor.close();
//			} catch(Exception e) {
//				LOGGER.error("Chained processor ? for scenario ? failed to close.", chainProcessor.getProcessorId(), getScenarioId(), e);
//			}
//			chainProcessor = null;
//		}
		
		try {
			cleanUp();
		} catch (Exception e) {
			LOGGER.error("Processor ? for scenario ? failed to close.", getProcessorId(), getScenarioId(), e);
		}
	}
	
	@Override
	public final void afterPropertiesSet() throws Exception {
		if(validProcessor(processorId)) {
			throw new ProcessorExistsException("Processor already exists " + processorId);
		}
		initProcessor();
	}
	
	public Processor getProcessorFromUUID(UUID processorId) throws IllegalProcessorRetrievalException {
		if(processorId == null) {
			return null;
		}
		if(processorId.equals(this.processorId) ||
				processorId.equals(this.chainProcessorId) ||
				processorId.equals(this.nextProcessorId) ||
				processorId.equals(this.returnProcessorId) ||
				this.referredProcessorIds.contains(processorId)) {
			return applicationContext.getBean(ProcessorManagementService.class).getProcessorById(processorId);
		}
		throw new IllegalProcessorRetrievalException();
	}
	
	public boolean validProcessor(UUID processorId) {
		if(processorId == null) {
			return false;
		}
		Processor p = applicationContext.getBean(ProcessorManagementService.class).getProcessorById(processorId);
		if(p == null) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AbstractProcessor [scenarioId=" + scenarioId + ", processorId=" + processorId + "]";
	}
	
}
