package com.cp.s2si.config;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.cp.s2si.jms.tasks.CacheEvictTopicTask;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

/**
 * The cache evict listener listening to single element evictions and send jms topics to other clustered auth modules.
 * 
 * @author panther
 *
 */
@Component
public class CacheEvictListener implements CacheEventListener {
	
	@Autowired @Qualifier("jmsTopicTemplate")
	private JmsTemplate jmsTopicTemplate;

	@Override
	public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
		CacheEvictTopicTask cett = new CacheEvictTopicTask((Serializable) element.getObjectKey());
		jmsTopicTemplate.convertAndSend(cett);
	}

	@Override
	public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
		// gulp
	}

	@Override
	public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
		CacheEvictTopicTask cett = new CacheEvictTopicTask((Serializable) element.getObjectKey());
		jmsTopicTemplate.convertAndSend(cett);
	}

	@Override
	public void notifyElementExpired(Ehcache cache, Element element) {
		// gulp
	}

	@Override
	public void notifyElementEvicted(Ehcache cache, Element element) {
		CacheEvictTopicTask cett = new CacheEvictTopicTask((Serializable) element.getObjectKey());
		jmsTopicTemplate.convertAndSend(cett);
	}

	@Override
	public void notifyRemoveAll(Ehcache cache) {
		// gulp
	}

	@Override
	public void dispose() {
		// gulp
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
