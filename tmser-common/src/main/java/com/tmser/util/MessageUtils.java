/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.util;

import org.springframework.context.MessageSource;

import java.util.Locale;

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

    private static MessageSource getMessageSource() {
        if (messageSource == null) {
            messageSource = SpringContextHolder.getBean(MessageSource.class);
        }
        return messageSource;
    }

    /**
     * 根据消息键和参数 获取消息
     * 委托给spring messageSource
     *
     * @param code       消息键
     * @param defaultMsg 默认消息
     * @param args       参数
     * @return
     */
    public static String message(String code, String defaultMsg, Object[] args, Locale locale) {
        MessageSource messageSource = getMessageSource();
        if (messageSource != null) {
            return messageSource.getMessage(code, args, defaultMsg, locale);
        }
        return defaultMsg;
    }

    /**
     * 根据消息键和参数 获取消息
     * 委托给spring messageSource
     *
     * @param code 消息键
     * @return
     */
    public static String message(String code) {
        return message(code, "", null, null);
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
        return message(code, "", args, null);
    }

    /**
     * 根据消息键和参数 获取消息
     * 委托给spring messageSource
     *
     * @param code 消息键
     * @return
     */
    public static String message(String code, String defaultMessage) {
        return message(code, defaultMessage, null, null);
    }

    /**
     * 根据消息键和参数 获取消息
     * 委托给spring messageSource
     *
     * @param code 消息键
     * @return
     */
    public static String message(String code, String defaultMessage, Object[] args) {
        return message(code, defaultMessage, args, null);
    }
}
