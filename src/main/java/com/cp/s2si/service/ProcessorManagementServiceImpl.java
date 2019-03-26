package com.cp.s2si.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.cp.s2si.persistence.ProcessorRepository;
import com.cp.s2si.persistence.models.ProcessorMainModel;
import com.cp.s2si.service.processors.Processor;
import com.cp.s2si.service.properties.BaseP;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author panther
 *
 */
@Service
public class ProcessorManagementServiceImpl implements ProcessorManagementService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorManagementServiceImpl.class);
	
	private static final Map<UUID, Processor> PROCESSOR_MAP = new ConcurrentHashMap<>();
	
	@Autowired private ApplicationContext applicationContext;
	@Autowired private ProcessorRepository processorRepository;
	
	private boolean isRunning = false;
	
	@EventListener
    public synchronized void start(ContextRefreshedEvent event) {
		if(!isRunning) {
			processorRepository.findAll().forEach(procMain -> {
				try {
					if(procMain.isActive()) {
						loadProcessor(procMain);
					}
				} catch (ClassNotFoundException | IOException e) {
					LOGGER.error("Loading processor failed with exception {}", e);
				}
			});
		}
		isRunning = true;
	}
	
	@SuppressWarnings("unchecked")
	private void loadProcessor(ProcessorMainModel procMain) throws ClassNotFoundException, JsonParseException, JsonMappingException, IOException {
		LOGGER.info("Trying to load processor {}", procMain);
		Class<? extends BaseP> propertyClass = (Class<? extends BaseP>) Class.forName(procMain.getPropertyClass());
		ObjectMapper om = new ObjectMapper();
		BaseP props = om.readValue(procMain.getPropertyJson(), propertyClass);
		props.setApplicationContext(applicationContext);
		props.setProcessorId(procMain.getProcessorId());
		props.setChainProcessorId(procMain.getChainProcessorId());
		props.setNextProcessorId(procMain.getNextProcessorId());
		props.setReturnProcessorId(procMain.getReturnProcessorId());
		props.setScenarioId(procMain.getScenarioId());
		props.setReferredProcessorIds(
				new HashSet<UUID>(
						procMain.getReferredProcs()
						.stream().map(map -> map.getRefPK().getReferredProcessorId())
						.collect(Collectors.toList())
				)
		);
		//Load should never return null. It should throw exception if load failed.
		Processor processor = props.load(); 
		PROCESSOR_MAP.put(procMain.getProcessorId(), processor);
		LOGGER.info("Processor {} loaded successfully", procMain);
	}
	
	public void loadProcessorById(UUID processorId) {
//		processorRepository.findById(processorId).ifPresent(procMain -> {
//			if(procMain.isActive()) {
//				loadProcessor(procMain);
//			}
//		});
	}
	
	public void unLoadProcessorById(UUID processorId) {
//		processorRepository.findById(processorId).ifPresent(procMain -> {
//			if(procMain.isActive()) {
//				loadProcessor(procMain);
//			}
//		});
	}

	@Override
	public Processor getProcessorById(UUID processorId) {
		if(!PROCESSOR_MAP.containsKey(processorId)) {
			return null;
		}
		return PROCESSOR_MAP.get(processorId);
	}

}
