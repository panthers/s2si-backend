package com.cp.s2si.service;

import java.util.UUID;

import com.cp.s2si.service.processors.Processor;

/**
 * 
 * @author panther
 *
 */
public interface ProcessorManagementService {
	
	public Processor getProcessorById(UUID processorId);

}
