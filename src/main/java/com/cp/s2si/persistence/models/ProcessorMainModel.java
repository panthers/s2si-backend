package com.cp.s2si.persistence.models;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * 
 * @author panther
 *
 */
@Entity @Table(name = "S2SI_PROC_MAIN")
public class ProcessorMainModel implements Serializable {

	private static final long serialVersionUID = -6870321439004890738L;
	
	@Id @Column(name = "PROCESSORID")
	private UUID processorId;
	
	@Column(name = "SCENARIOID")
	private Long scenarioId;
	
	@Column(name = "NEXT_PROCESSORID")
	private UUID nextProcessorId;
	
	@Column(name = "RETURN_PROCESSORID")
	private UUID returnProcessorId;
	
	@Column(name = "CHAIN_PROCESSORID")
	private UUID chainProcessorId;
	
	@Column(name = "DESCRIPTION")
	@Type(type = "com.cp.s2si.persistence.type.CustomStringNVarcharType")
	private String description;
	
	@Column(name = "PROP_CLASS")
	private String propertyClass;

	@Column(name = "PROC_CLASS")
	private String procesorClass;
	
	@Column(name = "PROP_JSON")
	@Lob
	private String propertyJson;
	
	@Column(name = "IS_ACTIVE")
	private boolean isActive;
	
	@OneToMany(mappedBy = "mainProcessor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<ProcessorRefModel> referredProcs;

	public UUID getProcessorId() {
		return processorId;
	}

	public void setProcessorId(UUID processorId) {
		this.processorId = processorId;
	}

	public Long getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(Long scenarioId) {
		this.scenarioId = scenarioId;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPropertyClass() {
		return propertyClass;
	}

	public void setPropertyClass(String propertyClass) {
		this.propertyClass = propertyClass;
	}

	public String getProcesorClass() {
		return procesorClass;
	}

	public void setProcesorClass(String procesorClass) {
		this.procesorClass = procesorClass;
	}

	public String getPropertyJson() {
		return propertyJson;
	}

	public void setPropertyJson(String propertyJson) {
		this.propertyJson = propertyJson;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public List<ProcessorRefModel> getReferredProcs() {
		return referredProcs;
	}

	public void setReferredProcs(List<ProcessorRefModel> referredProcs) {
		this.referredProcs = referredProcs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chainProcessorId == null) ? 0 : chainProcessorId.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + ((nextProcessorId == null) ? 0 : nextProcessorId.hashCode());
		result = prime * result + ((procesorClass == null) ? 0 : procesorClass.hashCode());
		result = prime * result + ((processorId == null) ? 0 : processorId.hashCode());
		result = prime * result + ((propertyClass == null) ? 0 : propertyClass.hashCode());
		result = prime * result + ((propertyJson == null) ? 0 : propertyJson.hashCode());
		result = prime * result + ((referredProcs == null) ? 0 : referredProcs.hashCode());
		result = prime * result + ((returnProcessorId == null) ? 0 : returnProcessorId.hashCode());
		result = prime * result + ((scenarioId == null) ? 0 : scenarioId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessorMainModel other = (ProcessorMainModel) obj;
		if (chainProcessorId == null) {
			if (other.chainProcessorId != null)
				return false;
		} else if (!chainProcessorId.equals(other.chainProcessorId))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (isActive != other.isActive)
			return false;
		if (nextProcessorId == null) {
			if (other.nextProcessorId != null)
				return false;
		} else if (!nextProcessorId.equals(other.nextProcessorId))
			return false;
		if (procesorClass == null) {
			if (other.procesorClass != null)
				return false;
		} else if (!procesorClass.equals(other.procesorClass))
			return false;
		if (processorId == null) {
			if (other.processorId != null)
				return false;
		} else if (!processorId.equals(other.processorId))
			return false;
		if (propertyClass == null) {
			if (other.propertyClass != null)
				return false;
		} else if (!propertyClass.equals(other.propertyClass))
			return false;
		if (propertyJson == null) {
			if (other.propertyJson != null)
				return false;
		} else if (!propertyJson.equals(other.propertyJson))
			return false;
		if (referredProcs == null) {
			if (other.referredProcs != null)
				return false;
		} else if (!referredProcs.equals(other.referredProcs))
			return false;
		if (returnProcessorId == null) {
			if (other.returnProcessorId != null)
				return false;
		} else if (!returnProcessorId.equals(other.returnProcessorId))
			return false;
		if (scenarioId == null) {
			if (other.scenarioId != null)
				return false;
		} else if (!scenarioId.equals(other.scenarioId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProcessorMainModel [processorId=" + processorId + ", scenarioId=" + scenarioId + ", nextProcessorId="
				+ nextProcessorId + ", returnProcessorId=" + returnProcessorId + ", chainProcessorId="
				+ chainProcessorId + ", description=" + description + ", propertyClass=" + propertyClass
				+ ", procesorClass=" + procesorClass + ", propertyJson=" + propertyJson + ", isActive=" + isActive
				+ ", referredProcs=" + referredProcs + "]";
	}
	
}
