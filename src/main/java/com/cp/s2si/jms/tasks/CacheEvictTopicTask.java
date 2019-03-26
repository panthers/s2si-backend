package com.cp.s2si.jms.tasks;

import java.io.Serializable;
import java.util.UUID;

import com.cp.s2si.config.S2SIProperties;

/**
 * Cache evict topic task is a topic message to all servers in a cluster to evict the provided id from ehcache.
 * UUID is used to identify the originating server of eviction so that eviction is not called again.
 * If eviction gets called again, there will never be any caching of data.
 * 
 * @author panther
 *
 */
public class CacheEvictTopicTask implements ITopicTask {

	private static final long serialVersionUID = -5544942528299882023L;
	
	private Serializable id;
	
	private UUID originalServerUUID = S2SIProperties.RANDOM_SERVER_UUID;
	
	public CacheEvictTopicTask(Serializable id) {
		this.id = id;
	}

	public Serializable getId() {
		return id;
	}
	
	public UUID getOriginalServerUUID() {
		return originalServerUUID;
	}
	
}
