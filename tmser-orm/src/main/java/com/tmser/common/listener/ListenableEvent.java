/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.common.listener;

import java.util.EventObject;

/**
 * <pre>
 *
 * </pre>
 *
 * @author tmser
 * @version $Id: ListenableEvent.java, v 1.0 2016年7月27日 下午1:44:49 tmser Exp $
 */
public class ListenableEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
    // ----------------------------------------------------------- Constructors

    /**
     * Construct a new ListenableEvent with the specified parameters.
     *
     * @param lifecycle Component on which this event occurred
     * @param type Event type (required)
     * @param data Event data (if any)
     */
    public ListenableEvent(Listenable listenable, String type, Object data) {

        super(listenable);
        this.type = type;
        this.data = data;
    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The event data associated with this event.
     */
    private Object data = null;


    /**
     * The event type this instance represents.
     */
    private String type = null;


    // ------------------------------------------------------------- Properties


    /**
     * Return the event data of this event.
     */
    public Object getData() {

        return (this.data);

    }


    /**
     * Return the Lifecycle on which this event occurred.
     */
    public Listenable getListenable() {

        return (Listenable) getSource();

    }


    /**
     * Return the event type of this event.
     */
    public String getType() {
        return (this.type);
    }

}
