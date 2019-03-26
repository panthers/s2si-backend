package com.cp.s2si.service.properties.string;

import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.string.XPathToBoolean;
import com.cp.s2si.service.properties.BaseP;

/**
 * 
 * @author panther
 *
 */
public class XPathToBooleanP extends BaseP {

	private String xpathTrue;
	private String xpathFalse;
	private boolean defaultBool;
	
	@Override
	public Processor load() {
		return getApplicationContext().getBean(XPathToBoolean.class, getScenarioId(), getProcessorId(), 
				getChainProcessorId(), getReturnProcessorId(), getNextProcessorId(), getReferredProcessorIds(),
				getXpathTrue(), getXpathFalse(), isDefaultBool());
	}

	public String getXpathTrue() {
		return xpathTrue;
	}

	public void setXpathTrue(String xpathTrue) {
		this.xpathTrue = xpathTrue;
	}

	public String getXpathFalse() {
		return xpathFalse;
	}

	public void setXpathFalse(String xpathFalse) {
		this.xpathFalse = xpathFalse;
	}

	public boolean isDefaultBool() {
		return defaultBool;
	}

	public void setDefaultBool(boolean defaultBool) {
		this.defaultBool = defaultBool;
	}

}
