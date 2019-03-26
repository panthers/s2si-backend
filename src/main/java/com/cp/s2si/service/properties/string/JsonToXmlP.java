package com.cp.s2si.service.properties.string;

import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.string.JsonToXml;
import com.cp.s2si.service.properties.BaseP;

/**
 * 
 * @author panther
 *
 */
public class JsonToXmlP extends BaseP {

	@Override
	public Processor load() {
		return getApplicationContext().getBean(JsonToXml.class, getScenarioId(), getProcessorId(), 
				getChainProcessorId(), getReturnProcessorId(), getNextProcessorId(), getReferredProcessorIds());
	}

}