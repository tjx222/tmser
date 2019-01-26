package com.tmser.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候取出ApplicaitonContext.
 * 
 * @author tjx
 * @date 2013-12-30
 */
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

	private static ApplicationContext applicationContext = null;

	private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 * @param requiredType
	 * @return
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		T t = null;
		try {
			t = applicationContext.getBean(requiredType);
		} catch (Exception e) {
			//do nothing
		}
		if(t == null){
			 String[] names = applicationContext.getBeanNamesForType(requiredType);
			 if(names.length == 0 && applicationContext instanceof  ConfigurableApplicationContext){
				 BeanFactory bf = ((ConfigurableApplicationContext)applicationContext).getBeanFactory().getParentBeanFactory();
				 if(bf instanceof ListableBeanFactory){
					 names = ((ListableBeanFactory)bf).getBeanNamesForType(requiredType);
				 }
		 }
		 for(String name : names){
			 t = applicationContext.getBean(name,requiredType);
			 break;
		 }
		}
		return t;
	}
	
	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 * 
	 * @param requiredType
	 * @return
	 */
	public static <T> T getBeanDefaultNull(Class<T> requiredType) {
		assertContextInjected();
		try {
			return getBean(requiredType);
		} catch (BeansException e) {
		}
		return null;
	}

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	public static void clearHolder() {
		logger.debug("清除SpringContextHolder中的ApplicationContext:"
				+ applicationContext);
		applicationContext = null;
	}

	/**
	 * 实现ApplicationContextAware接口, 注入Context到静态变量中.
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {

		if (SpringContextHolder.applicationContext != null) {
			logger.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:" + SpringContextHolder.applicationContext);
		}

		SpringContextHolder.applicationContext = applicationContext; // NOSONAR
	}

	/**
	 * 实现DisposableBean接口, 在Context关闭时清理静态变量.
	 */
	@Override
	public void destroy() throws Exception {
		SpringContextHolder.clearHolder();
	}

	/**
	 * 检查ApplicationContext不为空.
	 */
	private static void assertContextInjected() {
		Validate.validState(applicationContext != null, "applicaitonContext属性未注入, 请在applicationContext.xml中定义SpringContextHolder.");
	}
	
	public static <T> List<T> getBeansForType(Class<T> requiredType){
		 assertContextInjected();
		 List<T> rs = new ArrayList<T>();
		 Set<String> beanNames = new HashSet<String>();
		 String[] names = applicationContext.getBeanNamesForType(requiredType);
		 if(names != null){
			 beanNames.addAll(Arrays.asList(names));
		 }
		 
		 if(applicationContext instanceof  ConfigurableApplicationContext){
				 BeanFactory bf = ((ConfigurableApplicationContext)applicationContext).getBeanFactory().getParentBeanFactory();
				 if(bf instanceof ListableBeanFactory){
					 beanNames.addAll(Arrays.asList(((ListableBeanFactory)bf).getBeanNamesForType(requiredType)));
				 }
				
		 }
		 
		 for(String name : beanNames){
			 rs.add(applicationContext.getBean(name,requiredType));
		 }
		 
		 return rs;
	}
}