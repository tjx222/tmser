/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.utils;

import org.springframework.context.MessageSource;

/**
 * <pre>
 * 消息格式话工具
 * </pre>
 *
 * @author tmser
 * @version $Id: MessageUtils.java, v 1.0 2015年1月23日 下午7:12:24 tmser Exp $
 */
public class MessageUtils {
	private static MessageSource messageSource;
	
	private static MessageSource getMessageSource(){
	      if (messageSource == null) {
	            messageSource = SpringContextHolder.getBean(MessageSource.class);
	        }
	      return messageSource;
	}

    /**
     * 根据消息键和参数 获取消息
     * 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return
     */
    public static String message(String code, Object[] args) {
        return getMessageSource().getMessage(code, args,"", null);
    }
    
    /**
     * 根据消息键和参数 获取消息
     * 委托给spring messageSource
     *
     * @param code 消息键
     * @param defaultMsg 默认消息
     * @param args 参数
     * @return
     */
    public static String message(String code,String defaultMsg,Object[] args) {
        return getMessageSource().getMessage(code, args,defaultMsg, null);
    }
    
    /**
     * 根据消息键和参数 获取消息
     * 委托给spring messageSource
     *
     * @param code 消息键
     * @return
     */
    public static String message(String code) {
        return getMessageSource().getMessage(code, null,"", null);
    }
}
