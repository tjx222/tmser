/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.common.listener;

/**
 * <pre>
 *
 * </pre>
 *
 * @author tmser
 * @version $Id: Listener.java, v 1.0 2016年7月27日 下午1:36:57 tmser Exp $
 */
public interface Listener {
	
	  /**
     * Acknowledge the occurrence of the specified event.
     *
     * @param event LifecycleEvent that has occurred
     */
     void lifecycleEvent(ListenableEvent event);
     
     /**
      * Make sure this listener supports the event,
      * defaut supportsType is the entity class name
      * @param supportsType 
      * @return
      */
     boolean supports(Object[] supportsType);

}
