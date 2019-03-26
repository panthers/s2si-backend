package com.cp.s2si.jms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;
import javax.jms.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.cp.s2si.config.S2SIProperties;
import com.cp.s2si.jms.tasks.AddQueueTopicTask;
import com.cp.s2si.jms.tasks.CacheEvictTopicTask;
import com.cp.s2si.jms.tasks.IQueueTask;
import com.cp.s2si.jms.tasks.ITopicTask;
import com.cp.s2si.jms.tasks.RemoveQueueTopicTask;

import net.sf.ehcache.Cache;

/**
 * 
 * @author panther
 *
 */
@Component
public class Listeners {
	
	public static final String S2SI_TOPIC = "s2si-topic";
	public static final String S2SI_QUEUE = "s2si-queue";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Listeners.class);
	
	private static final Map<String, DefaultMessageListenerContainer> DYNAMIC_LISTENER_CONTAINERS = new ConcurrentHashMap<>();

	@Autowired private Cache cache;
	@Autowired private ConnectionFactory connectionFactory;
	@Autowired private ApplicationContext applicationContext;
	
	@JmsListener(destination = S2SI_TOPIC, containerFactory = "jmsTopicListenerContainerFactory", id = S2SI_TOPIC) 
    public void s2siTopic(ITopicTask task) {
		// Check instance type of task and consume in a service
		LOGGER.info("Single S2SI topic task {}", task);
		if(task instanceof AddQueueTopicTask) {
			addQueue((AddQueueTopicTask) task);
		} else if(task instanceof RemoveQueueTopicTask) {
			removeQueue(((RemoveQueueTopicTask) task).getQueueName());
		} else if(task instanceof CacheEvictTopicTask) {
			evictCache(((CacheEvictTopicTask) task));
		}
    }
	
	@JmsListener(destination = S2SI_QUEUE) 
    public void s2siQueue(IQueueTask task) {
		//Check instance type of task and consume in a service
		LOGGER.info("Single S2SI queue task {}", task);
    }
	
	@PreDestroy
	public void unloadDynamicQueues() {
		for(String queueName : DYNAMIC_LISTENER_CONTAINERS.keySet()) {
			DYNAMIC_LISTENER_CONTAINERS.get(queueName).stop();
			DYNAMIC_LISTENER_CONTAINERS.get(queueName).destroy();
		}
		DYNAMIC_LISTENER_CONTAINERS.clear();
	}
	
	private void addQueue(AddQueueTopicTask task) {
		if(DYNAMIC_LISTENER_CONTAINERS.containsKey(task.getQueueName())) {
			LOGGER.error("Queue {} already exists", task.getQueueName());
			return;
		}
		DefaultMessageListenerContainer newQueue = new DefaultMessageListenerContainer();
		newQueue.setConnectionFactory(connectionFactory);
		newQueue.setDestinationName(task.getQueueName());
		newQueue.setMessageListener(applicationContext.getBean(DynamicQueueMessageListener.class, task.getQueueConsumerProcessorId()));
		newQueue.afterPropertiesSet();
		newQueue.start();
		DYNAMIC_LISTENER_CONTAINERS.put(task.getQueueName(), newQueue);
	}
	
	private void removeQueue(String queueName) {
		queueName = queueName.toUpperCase();
		if(!DYNAMIC_LISTENER_CONTAINERS.containsKey(queueName)) {
			LOGGER.error("Queue {} does not exist", queueName);
			return;
		}
		DYNAMIC_LISTENER_CONTAINERS.get(queueName).stop();
		DYNAMIC_LISTENER_CONTAINERS.get(queueName).destroy();
		DYNAMIC_LISTENER_CONTAINERS.remove(queueName);
	}
	
	private void evictCache(CacheEvictTopicTask cett) {
		//Skip if task from same server
		if(cett.getOriginalServerUUID().equals(S2SIProperties.RANDOM_SERVER_UUID)) {
			return;
		}
		cache.removeQuiet(cett.getId());
	}

}