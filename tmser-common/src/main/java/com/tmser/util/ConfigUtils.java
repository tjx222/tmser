package com.tmser.util;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tmser.spring.SpringApplicationContext;
import org.springframework.core.env.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author tmser
 * @version 1.0
 * @title config封装调用方法, 支持各种动态配置
 * @description
 * @changeRecord
 */
public class ConfigUtils {

    private static final Map<String, PropertySourcesPropertyResolver> NAMESPACE_PROPERTIES_MAP = Maps.newHashMap();
    private static Environment config;


    /**
     * 默认值为null的获取value值
     *
     * @param namespace 命名空间
     * @param key       key值
     * @return
     */
    public static String get(String namespace, String key) {
        return get(namespace, key, null);
    }

    /**
     * 默认值自行传递
     *
     * @param namespace    配置名称
     * @param key          配置名
     * @param defaultValue 默认值
     * @return
     */
    public static String get(String namespace, String key, String defaultValue) {
        return getProperty(namespace, key, String.class, defaultValue);
    }


    /**
     * 获取boolean的value
     *
     * @param namespace
     * @param key
     * @return
     */
    public static Boolean getBoolean(String namespace, String key) {
        return getBoolean(namespace, key, null);
    }

    /**
     * 获取boolean的value，默认值为自行传递
     *
     * @param namespace
     * @param key
     * @param defaultValue
     * @return
     */
    public static Boolean getBoolean(String namespace, String key, Boolean defaultValue) {
        return getProperty(namespace, key, Boolean.class, defaultValue);
    }

    /**
     * 获取Long的value
     *
     * @param namespace
     * @param key
     * @return
     */
    public static Long getLong(String namespace, String key) {
        return getLong(namespace, key, null);
    }

    /**
     * 获取Long的value，默认值用户自行传递
     *
     * @param namespace
     * @param key
     * @return
     */
    public static Long getLong(String namespace, String key, Long defaultValue) {
        return getProperty(namespace, key, Long.class, defaultValue);
    }

    /**
     * 获取Long的value
     *
     * @param namespace
     * @param key
     * @return
     */
    public static Integer getInteger(String namespace, String key) {
        return getInteger(namespace, key, null);
    }

    /**
     * 获取Long的value
     *
     * @param namespace
     * @param key
     * @return
     */
    public static Integer getInteger(String namespace, String key, Integer defaultValue) {
        return getProperty(namespace, key, Integer.class, defaultValue);
    }

    /**
     * 获取flot的value，默认值用户自行传递
     *
     * @param namespace
     * @param key
     * @return
     */
    public static Float getFloat(String namespace, String key, Float defaultValue) {
        return getProperty(namespace, key, Float.class, defaultValue);
    }

    /**
     * 获取flot的value
     *
     * @param namespace
     * @param key
     * @return
     */
    public static Float getFloat(String namespace, String key) {
        return getFloat(namespace, key, null);
    }


    private static <T> T getProperty(String namespace, String key, Class<T> tClass, T defaultValue) {
        if (StringUtils.isNotEmpty()) {
            PropertySourcesPropertyResolver propertySourcesPropertyResolver = NAMESPACE_PROPERTIES_MAP.get(namespace);
            if (Objects.nonNull(propertySourcesPropertyResolver)) {
                return propertySourcesPropertyResolver.getProperty(key, tClass, defaultValue);
            }
        }

        if (config == null) {
            config = SpringApplicationContext.getApplicationContext().getEnvironment();
        }

        if (StringUtils.isNotEmpty(namespace)) {
            PropertySource<?> propertySource = ((ConfigurableEnvironment) config).getPropertySources().get(namespace);
            if (Objects.nonNull(propertySource)) {
                PropertySourcesPropertyResolver psv = new PropertySourcesPropertyResolver(new PropertySources() {
                    List<PropertySource<?>> propertySourceList = Lists.newArrayList(propertySource);

                    @Override
                    public Iterator<PropertySource<?>> iterator() {
                        return this.propertySourceList.iterator();
                    }

                    @Override
                    public boolean contains(String name) {
                        return true;
                    }

                    @Override
                    public PropertySource<?> get(String name) {
                        return propertySource;
                    }
                });

                NAMESPACE_PROPERTIES_MAP.put(namespace, psv);
                return psv.getProperty(key, tClass, defaultValue);
            }
        }

        T property = StringUtils.isNotEmpty(namespace) ? config.getProperty(namespace +"." + key, tClass) : null;

        return Objects.nonNull(property) ? property : config.getProperty(key, tClass, defaultValue);
    }

}
