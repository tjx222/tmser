package com.tmser.core;

import com.google.common.collect.ImmutableMap;

public final class ServerLocalContext {
    private final int pid;
    private final String localIp;
    private final String logDir;
    private final String env;
    private final ImmutableMap<String, String> appConfig;

    private final ImmutableMap<String, String> envConfig;

    public ServerLocalContext(int pid, String localIp, String logDir, String env, ImmutableMap<String, String> appConfig, ImmutableMap<String, String> envConfig) {
        this.pid = pid;
        this.localIp = localIp;
        this.logDir = logDir;
        this.env = env;
        this.appConfig = appConfig;
        this.envConfig = envConfig;
    }

    public int getPid() {
        return pid;
    }

    public String getLocalIp() {
        return localIp;
    }

    public String getLogDir() {
        return logDir;
    }

    public String getEnv() {
        return env;
    }

    public ImmutableMap<String, String> getAppConfig() {
        return appConfig;
    }

    public ImmutableMap<String, String> getEnvConfig() {
        return envConfig;
    }
}
