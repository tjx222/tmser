/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.common.listener;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

/**
 * <pre>
 *  实体监听启动器
 * </pre>
 *
 * @author tmser
 * @version $Id: ListenableBeanPostProcessor.java, v 1.0 2016年7月27日 下午5:21:33 tmser Exp $
 */
public class ListenableBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
												implements PriorityOrdered, BeanFactoryAware {
	
	private int order = Ordered.LOWEST_PRECEDENCE - 5;
	
	private ConfigurableListableBeanFactory beanFactory;

	/**
	 * @return
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	@Override
	public int getOrder() {
		return this.order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException{
		if(bean instanceof Listenable){
			Listenable listenable = (Listenable) bean;
			for(String name : beanFactory.getBeanNamesForType(Listener.class)){
				listenable.addListener(beanFactory.getBean(name, Listener.class));
			}
		}
		return bean;
	}

	/**
	 * @param beanFactory
	 * @throws BeansException
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof ConfigurableListableBeanFactory) {
			this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
		}
	}

}
