package com.cp.s2si.service.properties.string;

import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.string.StringPrint;
import com.cp.s2si.service.properties.BaseP;

public class StringPrintP extends BaseP {

	@Override
	public Processor load() {
		return getApplicationContext().getBean(StringPrint.class, getScenarioId(), getProcessorId(), 
				getChainProcessorId(), getReturnProcessorId(), getNextProcessorId(), getReferredProcessorIds());
	}

}
