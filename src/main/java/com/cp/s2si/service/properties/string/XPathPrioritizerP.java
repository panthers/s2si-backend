package com.cp.s2si.service.properties.string;

import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.string.XPathPrioritizer;
import com.cp.s2si.service.properties.BaseP;

/**
 * 
 * @author panther
 *
 */
public class XPathPrioritizerP extends BaseP {

	private String xpathHigh;
	private String xpathLow;
	
	@Override
	public Processor load() {
		return getApplicationContext().getBean(XPathPrioritizer.class, getScenarioId(), getProcessorId(), 
				getChainProcessorId(), getReturnProcessorId(), getNextProcessorId(), getReferredProcessorIds(),
				getXpathHigh(), getXpathLow());
	}

	public String getXpathLow() {
		return xpathLow;
	}

	public void setXpathLow(String xpathLow) {
		this.xpathLow = xpathLow;
	}

	public String getXpathHigh() {
		return xpathHigh;
	}

	public void setXpathHigh(String xpathHigh) {
		this.xpathHigh = xpathHigh;
	}

}
