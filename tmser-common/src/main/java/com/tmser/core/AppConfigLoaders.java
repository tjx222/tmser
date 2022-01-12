package com.tmser.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.HashMap;

public class AppConfigLoaders {
    private static final String LOCAL_LOADER = "local";
    private static final ImmutableMap<String, AppConfigLoader> LOADERS;

    public static AppConfigLoader getLoader(ServerLocalContext context) {
        String loader = loaderName(context);
        if(LOADERS.containsKey(loader)) {
            return (AppConfigLoader)LOADERS.get(loader);
        } else {
            throw new RuntimeException("暂不支持" + loader + "环境的AppConfig获取方式。");
        }
    }

    private static String loaderName(ServerLocalContext context) {
        return "local";
    }

    static {
        HashMap loaders = Maps.newHashMap();
        loaders.put("local", new LocalAppConfigLoader());
        LOADERS = ImmutableMap.copyOf(loaders);
    }
}
