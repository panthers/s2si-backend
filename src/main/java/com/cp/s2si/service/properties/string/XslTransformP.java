package com.cp.s2si.service.properties.string;

import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.string.XslTransform;
import com.cp.s2si.service.properties.BaseP;

/**
 * 
 * @author panther
 *
 */
public class XslTransformP extends BaseP {
	
	private String xsl;

	@Override
	public Processor load() {
		return getApplicationContext().getBean(XslTransform.class, getScenarioId(), getProcessorId(), 
				getChainProcessorId(), getReturnProcessorId(), getNextProcessorId(), getReferredProcessorIds(),
				getXsl());
	}

	public String getXsl() {
		return xsl;
	}

	public void setXsl(String xsl) {
		this.xsl = xsl;
	}

}
