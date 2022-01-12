package com.tmser.core;

import com.tmser.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalAppConfigLoader implements AppConfigLoader {
    private static final Logger LOG = LoggerFactory.getLogger(LocalAppConfigLoader.class);

    public AppConfig loadAppConfig(ServerLocalContext localContext) {
        if(localContext.getAppConfig().isEmpty()) {
            LOG.error("加载应用配置 app.properties 失败, 请先申请。。");
            return AppConfigCreator.createNullAppConfig(localContext);
        } else {
            LogUtil.log(new Object[] { "!!! 获取该应用的相关配置。" });
            return AppConfigCreator.createLocalAppConfig(localContext);
        }
    }
}
