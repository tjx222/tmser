/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.common.listener;

import java.util.Collection;

/**
 * <pre>
	实体生命周期
 * </pre>
 *
 * @author tmser
 * @version $Id: LifeCicyle.java, v 1.0 2016年7月27日 上午11:37:42 tmser Exp $
 */
public interface Listenable {
	

    /**
     * The ListenableEvent type for the "entity before save" event.
     * 数据为要插入实体
     */
    public static final String BEFORE_ADD_EVENT = "before_add";
    
    /**
     * The ListenableEvent type for the "entity after save" event.
     * 数据为插入后实体
     */
    public static final String AFTER_ADD_EVENT = "after_add";


    /**
     * The ListenableEvent type for the "entity before update" event.
     * 数据为要更新实体
     */
    public static final String BEFORE_UPDATE_EVENT = "before_update";
    
    /**
     * The ListenableEvent type for the "entity after update" event.
     * 数据为更新后实体
     */
    public static final String AFTER_UPDATE_EVENT = "after_update";
    

    /**
     * The ListenableEvent type for the "entity before delete" event.
     * 数据为要删除实体主键
     */
    public static final String BEFORE_DELETE_EVENT = "before_delete";
    
    /**
     * The ListenableEvent type for the "entity after delete" event.
     *  数据为删除实体主键
     */
    public static final String AFTER_DELETE_EVENT = "after_delete";
    
    /**
     * The ListenableEvent type for the "entity before delete" event.
     * 数据为更新前实体列表
     */
    public static final String BEFORE_BATCH_INSERT_EVENT = "before_batch_insert_delete";
    

	 /**
     * Add a Listener listener to this component.
     *
     * @param listener The listener to add
     */
    void addListener(Listener listener);
    
    /**
     * Get the life listeners. If this
     * component has no listeners registered, a empty collection is returned.
     */
    Collection<Listener> findListeners();


    /**
     * Remove a Listener listener from this component.
     *
     * @param listener The listener to remove
     */
    void removeListener(Listener listener);
    
    /**
     * 支持的类型
     * @return
     */
    Object[] supportTypes();
    
}
