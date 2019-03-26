package com.cp.s2si.service.processors.polling;

import java.util.Set;
import java.util.UUID;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cp.s2si.exception.MessageProcessingContinueException;
import com.cp.s2si.exception.MessageProcessingTerminateException;
import com.cp.s2si.exception.MessageValidationException;
import com.cp.s2si.service.processors.AbstractProcessor;
import com.cp.s2si.service.processors.ProcessorData;
import com.cp.s2si.service.properties.polling.PollingProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author panther
 *
 */
@Component
@Scope("prototype")
public class PathFilePolling extends AbstractProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PathFilePolling.class);
	
	@Autowired
	private Scheduler quartzScheduler;
	
	private String cronExpr;
	private PollingProperties props;
	
	private JobKey jobKey; //Generated Job Key 
	private TriggerKey triggerKey; //Generated Trigger Key
	
	public PathFilePolling(Long scenarioId, UUID processorId, UUID chainProcessorId, UUID returnProcessorId,
			UUID nextProcessorId, Set<UUID> referredProcessorIds, String cronExpr, PollingProperties props) {
		super(scenarioId, processorId, chainProcessorId, returnProcessorId, nextProcessorId, referredProcessorIds);
		this.cronExpr = cronExpr;
		this.props = props;
	}

	@Override
	public void validateMessage(ProcessorData message) throws MessageValidationException {
		// do nothing
	}

	@Override
	public ProcessorData process(ProcessorData message) throws MessageProcessingContinueException, MessageProcessingTerminateException {
		return message;
	}
	
	@Override
	public void initProcessor() throws Exception {
		ObjectMapper om = new ObjectMapper();
		JobDetail jd = JobBuilder
				.newJob(PathFilePollJob.class)
				.withIdentity("J_" + getProcessorId().toString())
				.withDescription("Scenario " + getScenarioId() + ", Processor Id " + getProcessorId())
				.usingJobData(PathFilePollJob.PROCESSOR_ID, getProcessorId().toString())
				.usingJobData(PathFilePollJob.PROPS, om.writeValueAsString(props))
				.build();
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("T_" + getProcessorId().toString())
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpr))
				.startNow()
				.build();
		
		jobKey = jd.getKey();
		triggerKey = trigger.getKey();
		
		if(quartzScheduler.checkExists(trigger.getKey())) {
			try {
				quartzScheduler.unscheduleJob(triggerKey);
			} catch (SchedulerException e) {
				LOGGER.error("Unschedule trigger error for processor id {} {}", getProcessorId(), e);
			}
		}
		try {
			quartzScheduler.scheduleJob(jd, trigger);
		} catch (SchedulerException e) {
			LOGGER.error("Scheduling and trigger error for processor id {} {}", getProcessorId(), e);
		}
		
		
	}

	@Override
	public void cleanUp() {
		try {
			quartzScheduler.unscheduleJob(triggerKey);
		} catch (SchedulerException e) {
			LOGGER.error("Unschedule trigger error for processor id {} {}", getProcessorId(), e);
		}
		try {
			quartzScheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			LOGGER.error("Delete job error for processor id {} {}", getProcessorId(), e);
		}
	}

}
