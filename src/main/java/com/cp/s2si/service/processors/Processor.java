package com.cp.s2si.service.processors;

import java.util.Set;
import java.util.UUID;

import com.cp.s2si.exception.IllegalProcessorRetrievalException;
import com.cp.s2si.exception.MessageProcessingContinueException;
import com.cp.s2si.exception.MessageProcessingTerminateException;
import com.cp.s2si.exception.MessageValidationException;

/**
 * 
 * @author panther
 *
 */
public interface Processor {
	
	/**
	 * @return Return the unique scenariodId for which this processor is used.
	 */
	Long getScenarioId();
	
	/**
	 * @return Return the unique processorid for this processor.
	 */
	UUID getProcessorId();
	
	/**
	 * @return Return the chain processorid for this processor.
	 */
	UUID getChainProcessorId();
	
	/**
	 * @return Return the return processorid for this processor.
	 */
	UUID getReturnProcessorId();
	
	/**
	 * @return Return the next processorid for this processor.
	 */
	UUID getNextProcessorId();
	
	/**
	 * @return Return the set of referred processorids for this processor.
	 */
	Set<UUID> getReferredProcessorIds();
	
	/**
	 * Main final method where life-cycle of the processor interface is declared.
	 * @param message
	 * @return
	 * @throws MessageValidationException
	 * @throws IllegalProcessorRetrievalException 
	 */
	ProcessorData run(ProcessorData message) throws MessageValidationException, IllegalProcessorRetrievalException, MessageProcessingTerminateException;
	
	/**
	 * Validate is the first life-cycle method of the run function.
	 * @param message
	 * @throws MessageValidationException
	 */
	void validateMessage(ProcessorData message) throws MessageValidationException;
	
	ProcessorData process(ProcessorData message) throws MessageProcessingContinueException, MessageProcessingTerminateException;
	
	void initProcessor() throws Exception;
	
	void close();
	
	void cleanUp();
	
}
