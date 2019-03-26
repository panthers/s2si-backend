package com.cp.s2si.jms.tasks;

/**
 * 
 * @author panther
 *
 */
public abstract class AbstractMessageOnQueueTask<E> implements IQueueTask {
	
	private static final long serialVersionUID = 6851227963646590237L;
	
	private Priority priority;

	@Override
	public Priority getTaskPriority() {
		return priority;
	}
	
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public abstract E getMessage();

}
