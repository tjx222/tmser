/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.common.cache.ehcache;

import org.springframework.cache.ehcache.EhCacheCache;

/**
 * <pre>
 *
 * </pre>
 *
 * @author 3020mt
 * @version $Id: Snippet.java, v 1.0 2016年10月18日 上午9:16:53 3020mt Exp $
 */
public class Ehcache extends EhCacheCache{
	
	/**
	 * @param ehcache
	 */
	public Ehcache(net.sf.ehcache.Ehcache ehcache) {
		super(ehcache);
	}

	@Override
	public ValueWrapper get(Object key) {
		return super.get(String.valueOf(key));
	}

	@Override
	public void put(Object key, Object value) {
		super.put(String.valueOf(key),value);
	}

	@Override
	public void evict(Object key) {
		super.evict(String.valueOf(key));
	}
}

