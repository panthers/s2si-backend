package com.cp.s2si.service.processors;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

import com.cp.s2si.jms.tasks.Priority;

/**
 * 
 * @author panther
 *
 */
public class ProcessorData implements Serializable {
	
	private static final long serialVersionUID = 1997066014009520774L;

	private boolean isStringData = false;
	private boolean isStreamData = false;
	private boolean isByteData = false;
	private boolean isPersisted = false;
	
	private Priority priority = Priority.MEDIUM;
	
	private String data;
	private InputStream stream;
	private byte[] byteData;
	
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
	public Priority getPriority() {
		return priority;
	}
	
	public void setStringData(String data) {
		this.isStringData = true;
		this.isStreamData = false;
		this.isByteData = false;
		this.stream = null; //Possible memory leak.
		this.byteData = null;
		this.data = data;
	}

	public String getStringData() {
		return data;
	}
	
	public void setByteData(byte[] data) {
		this.isStringData = false;
		this.isStreamData = false;
		this.isByteData = true;
		this.stream = null; //Possible memory leak.
		this.byteData = data;
		this.data = null;
	}
	
	public byte[] getByteData() {
		return byteData;
	}
	
	public void setStream(InputStream stream) {
		this.isStringData = false;
		this.isStreamData = true;
		this.isByteData = false;
		this.stream = stream;
		this.byteData = null;
		this.data = null;
	}
	
	public InputStream getStream() {
		return stream;
	}
	
	public boolean isStringData() {
		return isStringData;
	}

	public boolean isStreamData() {
		return isStreamData;
	}

	public boolean isByteData() {
		return isByteData;
	}
	
	public boolean isPersisted() {
		return isPersisted;
	}

	public void setPersisted(boolean isPersisted) {
		this.isPersisted = isPersisted;
	}

	@Override
	public String toString() {
		return "ProcessorData [\nisStringData=" + isStringData + ", \nisStreamData=" + isStreamData + ", \nisByteData="
				+ isByteData + ", \nisPersisted=" + isPersisted + ", \npriority=" + priority + ", \ndata=" + data
				+ ", \nbyteData=" + Arrays.toString(byteData) + "]";
	}

}
