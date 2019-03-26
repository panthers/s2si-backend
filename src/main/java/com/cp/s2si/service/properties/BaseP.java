package com.cp.s2si.service.properties;

import java.util.Set;
import java.util.UUID;

import org.springframework.context.ApplicationContext;

import com.cp.s2si.service.processors.Processor;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BaseP {
	
	@JsonIgnore
	private ApplicationContext applicationContext;
	@JsonIgnore
	private Long scenarioId;
	@JsonIgnore
	private UUID processorId;
	@JsonIgnore
	private UUID nextProcessorId;
	@JsonIgnore
	private UUID returnProcessorId;
	@JsonIgnore
	private UUID chainProcessorId;
	@JsonIgnore
	private Set<UUID> referredProcessorIds;
	
	public abstract Processor load();
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Long getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(Long scenarioId) {
		this.scenarioId = scenarioId;
	}

	public UUID getProcessorId() {
		return processorId;
	}

	public void setProcessorId(UUID processorId) {
		this.processorId = processorId;
	}

	public UUID getNextProcessorId() {
		return nextProcessorId;
	}

	public void setNextProcessorId(UUID nextProcessorId) {
		this.nextProcessorId = nextProcessorId;
	}

	public UUID getReturnProcessorId() {
		return returnProcessorId;
	}

	public void setReturnProcessorId(UUID returnProcessorId) {
		this.returnProcessorId = returnProcessorId;
	}

	public UUID getChainProcessorId() {
		return chainProcessorId;
	}

	public void setChainProcessorId(UUID chainProcessorId) {
		this.chainProcessorId = chainProcessorId;
	}

	public Set<UUID> getReferredProcessorIds() {
		return referredProcessorIds;
	}

	public void setReferredProcessorIds(Set<UUID> referredProcessorIds) {
		this.referredProcessorIds = referredProcessorIds;
	}
	
}
