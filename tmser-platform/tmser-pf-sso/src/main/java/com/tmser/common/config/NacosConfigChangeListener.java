package com.tmser.common.config;

import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.core.env.NacosPropertySource;
import com.tmser.config.ConfigChangedListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * nacos 配置更新监听
 */
@Service
@Slf4j
public class NacosConfigChangeListener {

    @NacosValue(value = "${test}", autoRefreshed = true)
    private String test;

    @Resource
    private ConfigChangedListener configChangedListener;

    public String getTest() {
        return this.test;
    }


    @NacosConfigListener(dataId = "sample")
    public void addConfigListener(String content) {
        log.info("new content: {}", content);
        NacosPropertySource propertySource = new NacosPropertySource("sample", "sample", "tmser-sample-tmpt", content, "yml");
        propertySource.getSource().forEach((k,v) ->{
            configChangedListener.onChange(String.valueOf(k), String.valueOf(v));
        });
    }


}
