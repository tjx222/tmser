package com.tmser.config;

import com.tmser.util.ConfigUtils;
import org.springframework.stereotype.Component;

/**
 * 通用的配置监听器，更新ConfigUtils 配置缓存
 */
@Component
public class CommonConfigChangeListener implements ConfigChangedListener {

    @Override
    public void onChange(String key, Object value) {
        Object oldValue = ConfigUtils.get(key);
        if (oldValue != null && !oldValue.equals(value)) { //存在变更再更新
            ConfigUtils.updateCache(key, value);
        }
    }
}
