package com.tmser.util;


import com.google.common.collect.Maps;
import com.tmser.spring.SpringApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author tmser
 * @version 1.0
 * @title config封装调用方法, 支持各种动态配置
 * @description 存在本地缓存，如有动态配置需事先相关的listener
 */
public class ConfigUtils {

    private static final Map<String, Object> CACHED_PROPERTIES_MAP = Maps.newConcurrentMap();
    private static Environment config;
    private static ConversionService conversionService;


    /**
     * 默认值为null的获取value值
     *
     * @param key key值
     * @return 配置值
     */
    public static String get(String key) {
        return get(key, null);
    }

    /**
     * 默认值自行传递
     *
     * @param key          配置名
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static String get(String key, String defaultValue) {
        return getProperty(key, String.class, defaultValue);
    }


    /**
     * 获取boolean的value
     *
     * @param key 配置名
     * @return 配置值
     */
    public static Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    /**
     * 获取boolean的value，默认值为自行传递
     *
     * @param defaultValue 默认值
     * @param key          配置key
     * @return 配置值
     */
    public static Boolean getBoolean(String key, Boolean defaultValue) {
        return getProperty(key, Boolean.class, defaultValue);
    }

    /**
     * 获取Long的value
     *
     * @param key 配置key
     * @return 配置值
     */
    public static Long getLong(String key) {
        return getLong(key, null);
    }


    /**
     * 获取Long的value，默认值用户自行传递
     *
     * @param key 配置key
     * @return 配置值
     */
    public static Long getLong(String key, Long defaultValue) {
        return getProperty(key, Long.class, defaultValue);
    }

    /**
     * 获取Long的value
     *
     * @param key 配置key
     * @return 配置值
     */
    public static Integer getInteger(String key) {
        return getInteger(key, null);
    }


    /**
     * 获取Long的value
     *
     * @param key          配置key
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static Integer getInteger(String key, Integer defaultValue) {
        return getProperty(key, Integer.class, defaultValue);
    }


    /**
     * 获取flot的value，默认值用户自行传递
     *
     * @param key 配置key
     * @return 配置值
     */
    public static Float getFloat(String key) {
        return getFloat(key, null);
    }

    /**
     * 获取flot的value，默认值用户自行传递
     *
     * @param key 配置key
     * @return 配置值
     */
    public static Float getFloat(String key, Float defaultValue) {
        return getProperty(key, Float.class, defaultValue);
    }

    private static <T> T getProperty(final String key, Class<T> tClass, T defaultValue) {
        if (config == null) {
            config = SpringApplicationContext.getApplicationContext().getEnvironment();
            conversionService = SpringApplicationContext.getBean(ConversionService.class);
        }
        Object property = Optional.ofNullable(CACHED_PROPERTIES_MAP.get(key))
                .map(v -> {
                            if (v.getClass().equals(tClass)) {
                                return v;
                            }
                            T pv = convertValueIfNecessary(v, tClass);
                            if (pv != null) {
                                CACHED_PROPERTIES_MAP.put(key, pv);
                            }
                            return pv;
                        }
                )
                .orElseGet(() -> {
                    T v = config.getProperty(key, tClass, defaultValue);
                    if (v != null) {
                        CACHED_PROPERTIES_MAP.put(key, v);
                    }
                    return v;
                });

        return (T) property;
    }

    protected static <T> T convertValueIfNecessary(Object value, @Nullable Class<T> targetType) {
        if (targetType == null) {
            return (T) value;
        }
        ConversionService conversionServiceToUse = conversionService;
        if (conversionServiceToUse == null) {
            // Avoid initialization of shared DefaultConversionService if
            // no standard type conversion is needed in the first place...
            if (ClassUtils.isAssignableValue(targetType, value)) {
                return (T) value;
            }
            conversionServiceToUse = DefaultConversionService.getSharedInstance();
        }
        return conversionServiceToUse.convert(value, targetType);
    }

    /**
     * 更新配置缓存
     */
    public static void updateCache(String key, Object value) {
        if (Objects.nonNull(key) && Objects.nonNull(value)) {
            CACHED_PROPERTIES_MAP.put(key, value);
        }
    }

    /**
     * 清除配置缓存
     */
    public static void clearCache() {
        CACHED_PROPERTIES_MAP.clear();
    }

}
