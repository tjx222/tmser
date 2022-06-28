package com.tmser.config;

import com.tmser.util.CollectionUtils;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 组合配置监听器
 */
@Component
@Primary
public class CompositeConfigChangeListener implements ConfigChangedListener {

    @Setter
    @Resource
    private List<ConfigChangedListener> listenerList;


    @Override
    public void onChange(String key, Object value) {
        for (ConfigChangedListener l : listenerList) {
            if (l == this) {
                continue;
            }

            if (CollectionUtils.isEmpty(l.listenerKeys()) || l.listenerKeys().contains(key)) {
                l.onChange(key, value);
            }
        }
    }
}
