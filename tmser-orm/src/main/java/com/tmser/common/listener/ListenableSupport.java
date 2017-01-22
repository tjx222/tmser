/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.common.listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.tmser.common.bo.QueryObject;


/**
 * <pre>
 *
 * </pre>
 *
 * @author tmser
 * @version $Id: Snippet.java, v 1.0 2016年7月27日 下午1:40:26 tmser Exp $
 */
public abstract class ListenableSupport implements Listenable {
	
    /**
     * The set of registered ListenableListeners for event notifications.
     */
    private Listener listeners[] = new Listener[0];
    
    private final Object listenersLock = new Object(); // Lock object for changes to listeners

    /**
     * Add a  event listener to this component.
     *
     * @param listener The listener to add
     */
    @Override
	public void addListener(Listener listener) {
    	
      if(!listener.supports(this.supportTypes()))
    		return;

      synchronized (listenersLock) {
    	  Listener results[] =
            new Listener[listeners.length + 1];
          for (int i = 0; i < listeners.length; i++)
              results[i] = listeners[i];
          results[listeners.length] = listener;
          listeners = results;
      }

    }


    /**
     * Get the  listeners . If this 
     * Listenable has no listeners registered, a zero-length array is returned.
     */
    @Override
	public Collection<Listener> findListeners() {
        return Collections.unmodifiableCollection(Arrays.asList(listeners));
    }


    /**
     * Notify all event listeners that a particular event has
     * occurred for this Container.  The default implementation performs
     * this notification synchronously using the calling thread.
     *
     * @param type Event type
     * @param data Event data
     */
    protected void fireListenableEvent(String type, Object data) {
    	ListenableEvent event = new ListenableEvent(this, type, data);
        Listener interested[] = listeners;
        for (int i = 0; i < interested.length; i++)
            interested[i].lifecycleEvent(event);

    }
    
    /**
     * Notify all event listeners that a particular event has
     * occurred for this Container.  The default implementation performs
     * this notification synchronously using the calling thread.
     *
     * @param type Event type
     * @param data Event data
     */
	protected void fireListenableEvent(Class<QueryObject> supportType,String type, Object data) {
    	ListenableEvent event = new ListenableEvent(this, type, data);
        Listener interested[] = listeners;
        for (int i = 0; i < interested.length; i++){
        	if(interested[i].supports(new String[]{supportType.getName()}))
        		interested[i].lifecycleEvent(event);
        }

    }


    /**
     * Remove a event listener from this component.
     *
     * @param listener The listener to remove
     */
    @Override
	public void removeListener(Listener listener) {

        synchronized (listenersLock) {
            int n = -1;
            for (int i = 0; i < listeners.length; i++) {
                if (listeners[i] == listener) {
                    n = i;
                    break;
                }
            }
            if (n < 0)
                return;
            Listener results[] =
              new Listener[listeners.length - 1];
            int j = 0;
            for (int i = 0; i < listeners.length; i++) {
                if (i != n)
                    results[j++] = listeners[i];
            }
            listeners = results;
        }

    }
    
}

