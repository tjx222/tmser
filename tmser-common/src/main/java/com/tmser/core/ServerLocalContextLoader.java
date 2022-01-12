package com.tmser.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.tmser.util.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ServerLocalContextLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ServerLocalContextLoader.class);

    ServerLocalContextLoader() {
    }

    ServerLocalContext loadLocalContext() {
        int pid = ServerManager.getPid();
        String localIp = IpUtils.getLocalAddress().getHostAddress();
        String logDir = buildLogDir();
        ImmutableMap<String, String> appProperties = readLocalConf("app.properties");
        ImmutableMap<String, String> envProperties = ImmutableMap.of();
        String env = "local";
        try {
            envProperties = readLocalConf("app-env.properties");
            env = envProperties.get("runEnv");
        } catch (Exception e) {
            LOG.warn("no file app-env.properties in profile", e);
        }
        return new ServerLocalContext(pid, localIp, logDir, env, appProperties, envProperties);
    }

    private static String buildLogDir() {
        String basePath = System.getProperty("catalina.base");
        if (basePath != null) {
            File logDirFile = new File(basePath, "logs");
            if (logDirFile.exists()) {
                return logDirFile.getAbsolutePath();
            }
        }

        return null;
    }

    private static ImmutableMap<String, String> readLocalConf(String filename) {
        ServerLocalContextLoader.ResourceConfig config = ServerLocalContextLoader.ResourceConfig.getOrNull(filename);
        return ImmutableMap.copyOf(config.getAll());
    }

    private static final class ResourceConfig {
        private final Map<String, String> data;

        static ServerLocalContextLoader.ResourceConfig getOrNull(String name) {
            try {
                return new ServerLocalContextLoader.ResourceConfig(name);
            } catch (ServerLocalContextLoader.DuplicateConfigException var2) {
                throw new RuntimeException("检测到重复配置文件", var2);
            } catch (Exception var3) {
                throw new RuntimeException("请检查配置文件");

            }
        }

        private ResourceConfig(String name) {

            Preconditions.checkArgument(!(name == null || name.trim().length() == 0), "配置文件名不能为空");
            this.forbidDuplicateConfig(name);
            try (InputStream res = Thread.currentThread().getContextClassLoader().getResourceAsStream(name)) {
                try {
                    Properties e = new Properties();
                    e.load(res);
                    this.data = this.fromProperties(e);
                } catch (Exception var7) {
                    throw new RuntimeException("无法读取配置文件：" + name, var7);
                }
            } catch (IOException e) {
                throw new RuntimeException("无法找到配置文件: " + name);
            }
        }

        private void forbidDuplicateConfig(String name) {
            try {
                ArrayList e = Collections.list(Thread.currentThread().getContextClassLoader().getResources(name));
                if (e.size() > 1) {
                    ServerLocalContextLoader.LOG.error("文件{}只允许有一个，但是发现多个，位置分别为: {}", name, e);
                    throw new ServerLocalContextLoader.DuplicateConfigException("配置文件" + name + "不能存在多个，地址分别为：" + e);
                }
            } catch (IOException var3) {
            }

        }

        private Map<String, String> fromProperties(Properties prop) {
            HashMap<String, String> map = Maps.newHashMap();
            Iterator i$ = prop.stringPropertyNames().iterator();

            while (i$.hasNext()) {
                String key = (String) i$.next();
                map.put(key, prop.getProperty(key));
            }
            return map;
        }

        /**
         * 检查 旧的apollo配置 更新为新的
         */
        Map<String, String> getAll() {
            return Collections.unmodifiableMap(this.data);
        }
    }

    private static class DuplicateConfigException extends RuntimeException {
        DuplicateConfigException(String message) {
            super(message);
        }
    }
}
