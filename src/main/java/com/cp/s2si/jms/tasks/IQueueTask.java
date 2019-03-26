package com.cp.s2si.jms.tasks;

import java.io.Serializable;

/**
 * Interface which will be implemented to send queue task across all instances of s2si cluster.
 * 
 * @author panther
 */
public interface IQueueTask extends Serializable {
	
	Priority getTaskPriority();

}
