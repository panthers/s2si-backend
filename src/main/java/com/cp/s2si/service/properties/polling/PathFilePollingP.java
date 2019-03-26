package com.cp.s2si.service.properties.polling;

import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.processors.polling.PathFilePolling;
import com.cp.s2si.service.properties.BaseP;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PathFilePollingP extends BaseP {
	
	private String cronExpr;
	private PollingProperties props;
	
	@Override
	public Processor load() {
		return getApplicationContext().getBean(PathFilePolling.class, getScenarioId(), getProcessorId(), 
				getChainProcessorId(), getReturnProcessorId(), getNextProcessorId(), getReferredProcessorIds(), getCronExpr(), getProps());
	}

	public String getCronExpr() {
		return cronExpr;
	}

	public void setCronExpr(String cronExpr) {
		this.cronExpr = cronExpr;
	}

	public PollingProperties getProps() {
		return props;
	}

	public void setProps(PollingProperties props) {
		this.props = props;
	}
	
	public static void main(String[] args) throws Exception {
		PathFilePollingP p = new PathFilePollingP();
		p.setCronExpr("0/5 * * * * * *");
		PollingProperties props = new PollingProperties();
		props.setPathToPoll("/Users/panther/1/poll");
		props.setCheckForAnyFileInFolderPath(true);
		props.setFileNameToCheck("poll.now");
		props.setFileNamePrefixToPickup("WO");
		props.setFileNameSuffixToPickup(".xml");
		props.setFileNameRegexToPickup("WO-\\d{5}.xml");
		props.setDeleteAfterPickup(false);
		props.setMoveAfterPickup(true);
		props.setMoveToPathOnSuccess("success");
		props.setMoveToPathOnFailure("error");
		props.setRenameFile(true);
		props.setRenamePrefix("PROCESSED_");
		props.setRenameNameSuffix("");
		props.setRenameExtensionSuffix("");
		props.setLockFilePreffix(".");
		props.setLockFileSuffix(".lock");
		props.setSizeCheckIntervalInMilliseconds(2000);
		p.setProps(props);
		System.out.println(new ObjectMapper().writeValueAsString(p));
	}
}
