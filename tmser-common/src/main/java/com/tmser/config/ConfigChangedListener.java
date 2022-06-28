package com.tmser.config;

import java.util.Collections;
import java.util.Set;

/**
 * 配置变更监听器
 */
public interface ConfigChangedListener {

    /**
     * 发生变更的配置
     *
     * @param key   配置key
     * @param value 变更后值
     */
    void onChange(String key, Object value);

    /**
     * 需要监听的key
     * 空，监听所有
     *
     * @return 需要监听的key
     */
    default Set<String> listenerKeys() {
        return Collections.emptySet();
    }

}
