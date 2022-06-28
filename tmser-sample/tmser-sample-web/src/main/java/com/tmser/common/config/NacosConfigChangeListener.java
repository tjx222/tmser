package com.tmser.common.config;

import com.tmser.config.ConfigChangedListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * nacos 配置更新监听
 */
@ConditionalOnClass(name="com.alibaba.nacos.api.config.annotation.NacosConfigListener")
@Service
@Slf4j
public class NacosConfigChangeListener {

    @Resource
    private ConfigChangedListener configChangedListener;


  //  @com.alibaba.nacos.api.config.annotation.NacosConfigListener(dataId = "sample")
    public void addConfigListener(String content) {
        log.info("new content: {}", content);
//        NacosPropertySource propertySource = new NacosPropertySource("sample", "sample", "tmser-sample-tmpt", content, "yml");
//        propertySource.getSource().forEach((k,v) ->{
//            configChangedListener.onChange(String.valueOf(k), String.valueOf(v));
//        });
    }


}
