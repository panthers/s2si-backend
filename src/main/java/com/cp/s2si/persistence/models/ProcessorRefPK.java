package com.cp.s2si.persistence.models;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ProcessorRefPK implements Serializable {

	private static final long serialVersionUID = -4613275214053985315L;
	
	@Column(name = "PROCESSORID")
	private UUID processorId;
	
	@Column(name = "REFERRED_PROCESSORID")
	private UUID referredProcessorId;

	public UUID getProcessorId() {
		return processorId;
	}

	public void setProcessorId(UUID processorId) {
		this.processorId = processorId;
	}

	public UUID getReferredProcessorId() {
		return referredProcessorId;
	}

	public void setReferredProcessorId(UUID referredProcessorId) {
		this.referredProcessorId = referredProcessorId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((processorId == null) ? 0 : processorId.hashCode());
		result = prime * result + ((referredProcessorId == null) ? 0 : referredProcessorId.hashCode());
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
		ProcessorRefPK other = (ProcessorRefPK) obj;
		if (processorId == null) {
			if (other.processorId != null)
				return false;
		} else if (!processorId.equals(other.processorId))
			return false;
		if (referredProcessorId == null) {
			if (other.referredProcessorId != null)
				return false;
		} else if (!referredProcessorId.equals(other.referredProcessorId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProcessorRefPK [processorId=" + processorId + ", referredProcessorId=" + referredProcessorId + "]";
	}
	
}
