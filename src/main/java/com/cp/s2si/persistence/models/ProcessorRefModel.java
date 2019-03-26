package com.cp.s2si.persistence.models;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * 
 * @author panther
 *
 */
@Entity @Table(name = "S2SI_PROC_REF")
public class ProcessorRefModel implements Serializable {

	private static final long serialVersionUID = -6870321439004890738L;
	
	@EmbeddedId
	private ProcessorRefPK refPK;
	
	@ManyToOne(cascade = CascadeType.ALL, optional = true)
	@JoinColumn(name = "PROCESSORID", referencedColumnName = "PROCESSORID", insertable = false, updatable = false)
	@LazyCollection(LazyCollectionOption.EXTRA)
	private ProcessorMainModel mainProcessor;

	public ProcessorRefPK getRefPK() {
		return refPK;
	}

	public void setRefPK(ProcessorRefPK refPK) {
		this.refPK = refPK;
	}

	public ProcessorMainModel getMainProcessor() {
		return mainProcessor;
	}

	public void setMainProcessor(ProcessorMainModel mainProcessor) {
		this.mainProcessor = mainProcessor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mainProcessor == null) ? 0 : mainProcessor.hashCode());
		result = prime * result + ((refPK == null) ? 0 : refPK.hashCode());
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
		ProcessorRefModel other = (ProcessorRefModel) obj;
		if (mainProcessor == null) {
			if (other.mainProcessor != null)
				return false;
		} else if (!mainProcessor.equals(other.mainProcessor))
			return false;
		if (refPK == null) {
			if (other.refPK != null)
				return false;
		} else if (!refPK.equals(other.refPK))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProcessorRefModel [refPK=" + refPK + ", mainProcessor=" + mainProcessor + "]";
	}
	
}
