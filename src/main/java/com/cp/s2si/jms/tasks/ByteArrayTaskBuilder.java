package com.cp.s2si.jms.tasks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cp.s2si.service.processors.ProcessorData;

/**
 * 
 * @author panther
 *
 */
@Component
public class ByteArrayTaskBuilder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ByteArrayTaskBuilder.class);
	
	//TODO Autowire the DB Hibernate DAO
	
	public ByteArrayMessageTask wrap(ProcessorData data) throws IOException {
		if(data.isStreamData()) {
			ByteArrayMessageTask task = buildWithStream(data.getStream());
			task.setPriority(data.getPriority());
			return task;
		} else if(data.isStringData()) {
			ByteArrayMessageTask task = buildWithString(data.getStringData());
			task.setPriority(data.getPriority());
			return task;
		} else if(data.isByteData()) {
			ByteArrayMessageTask task = buildWithStream(new ByteArrayInputStream(data.getByteData()));
			task.setPriority(data.getPriority());
			return task;
		}
		throw new IOException("Unsuitable data to generate task");
	}
	
	public ProcessorData unwrap(ByteArrayMessageTask task) {
		ProcessorData pd = new ProcessorData();
		pd.setPriority(task.getTaskPriority());
		if(!task.isDataPersisted) {
			if(task.isStringData) {
				pd.setStringData(new String(task.getData()));
			} else if(task.isByteData) {
				pd.setByteData(task.getData());
			}
		} else {
			pd.setStringData(new String(task.getData()));
			pd.setPersisted(true);
		}
		return pd;
	}
	
	private ByteArrayMessageTask buildWithString(String message) throws IOException {
		//Ignoring message length as it would be checked anyway during buildWithStream
		InputStream is = new ByteArrayInputStream(message.getBytes());
		ByteArrayMessageTask task = buildWithStream(is);
		task.isStringData = true;
		return task;
	}
	
	private ByteArrayMessageTask buildWithStream(InputStream source) throws IOException {
		
		byte[] sourceData = null;
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] temp_data = new byte[16384]; //16KB loads
		
		int loops = 0;
		boolean isDataTooBig = false;

		while ((nRead = source.read(temp_data, 0, temp_data.length)) != -1) {
			loops++;
			buffer.write(temp_data, 0, nRead);
			if(loops >= 1024) { //16MB loaded, file/data too big
				LOGGER.warn("Too many bytes, Skipping memory and using DB persistence.");
				isDataTooBig = true;
				break;
			}
		}
		buffer.flush();
		
		if(isDataTooBig) {
			sourceData = persistStream(source);
			ByteArrayMessageTask task = new ByteArrayMessageTask(sourceData, true);
			task.isByteData = true;
			return task;
		} else {
			sourceData = buffer.toByteArray();
			ByteArrayMessageTask task = new ByteArrayMessageTask(sourceData, false);
			task.isByteData = true;
			return task;
		}
	}
	
	private byte[] persistStream(InputStream source) {
		//TODO Save to database and get the random reference UUID
		return UUID.randomUUID().toString().getBytes();
	}
	
	public static class ByteArrayMessageTask extends AbstractMessageOnQueueTask<InputStream> {

		private static final long serialVersionUID = 8182436978044107734L;
		
		private byte[] data;
		
		private boolean isDataPersisted = false;
		private boolean isStringData = false;
		private boolean isByteData = false;
		
		private ByteArrayMessageTask(byte[] data, boolean isDataPersisted) {
			this.data = data;
			this.isDataPersisted = isDataPersisted;
		}
		
		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}

		public boolean isDataPersisted() {
			return isDataPersisted;
		}

		public void setDataPersisted(boolean isDataPersisted) {
			this.isDataPersisted = isDataPersisted;
		}
		
		public boolean isStringData() {
			return isStringData;
		}

		public boolean isByteData() {
			return isByteData;
		}

		@Override
		public InputStream getMessage() {
			return new ByteArrayInputStream(data);
		}
	}

}
