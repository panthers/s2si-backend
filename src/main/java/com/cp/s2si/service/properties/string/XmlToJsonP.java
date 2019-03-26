package com.cp.s2si.service.properties.string;

import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.string.XmlToJson;
import com.cp.s2si.service.properties.BaseP;

/**
 * 
 * @author panther
 *
 */
public class XmlToJsonP extends BaseP {

	@Override
	public Processor load() {
		return getApplicationContext().getBean(XmlToJson.class, getScenarioId(), getProcessorId(), 
				getChainProcessorId(), getReturnProcessorId(), getNextProcessorId(), getReferredProcessorIds());
	}

}