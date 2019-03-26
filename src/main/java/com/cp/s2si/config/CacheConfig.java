package com.cp.s2si.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;

/**
 * Ehcache configuration class for auth module.
 * 
 * @author panther
 *
 */
@Configuration
@EnableCaching
public class CacheConfig {
	
	@Autowired
	private CacheEvictListener cacheEvictListener;

	@Bean
	public CacheManager cacheManager() {
		EhCacheCacheManager eccm = new EhCacheCacheManager(ehCacheCacheManager().getObject());
		eccm.getCacheManager().addCache(cache());
		return eccm;
	}

	@Bean
	public EhCacheManagerFactoryBean ehCacheCacheManager() {
		EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
		cmfb.setConfigLocation(new ClassPathResource("ehcache.xml"));
		cmfb.setShared(true);
		return cmfb;
	}
	
	@Bean
	public Cache cache() {
		CacheConfiguration authCacheConfig = new CacheConfiguration();
		authCacheConfig.setName(S2SIProperties.MODULE_NAME);
		authCacheConfig.setMaxEntriesLocalHeap(5000);
		authCacheConfig.setEternal(false);
		authCacheConfig.setTimeToIdleSeconds(4500);
		authCacheConfig.setTimeToLiveSeconds(9000);
		authCacheConfig.persistence(new PersistenceConfiguration().strategy(Strategy.NONE));
		Cache authCache = new Cache(authCacheConfig);
		authCache.getCacheEventNotificationService().registerListener(cacheEvictListener);
		return authCache;
	}

}
