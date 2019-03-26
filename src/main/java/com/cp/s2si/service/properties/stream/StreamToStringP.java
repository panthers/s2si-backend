package com.cp.s2si.service.properties.stream;

import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.stream.StreamToString;
import com.cp.s2si.service.properties.BaseP;

/**
 * 
 * @author panther
 *
 */
public class StreamToStringP extends BaseP {

	@Override
	public Processor load() {
		return getApplicationContext().getBean(StreamToString.class, getScenarioId(), getProcessorId(), 
				getChainProcessorId(), getReturnProcessorId(), getNextProcessorId(), getReferredProcessorIds());
	}

}