package com.tmser.core;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class GlobalEnvWriter {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalEnvWriter.class);
    private final ImmutableMap<String, String> globalEnv;

    public GlobalEnvWriter(ImmutableMap<String, String> globalEnv) {
        this.globalEnv = globalEnv;
    }

    public void writeToSystem() {
        Iterator i$ = this.globalEnv.keySet().iterator();

        while(i$.hasNext()) {
            String key = (String)i$.next();
            String oldValue = System.getProperty(key);
            if(oldValue == null) {
                System.setProperty(key, (String)this.globalEnv.get(key));
            } else {
                LOG.warn("系统属性中已经存在key为{}的属性，值为{}，跳过写入GlobalEnv中的此属性。", key, oldValue);
            }
        }

    }
}
