package com.tmser.core;

public final class ServerContext {
    private final AppConfig appConfig;

    public ServerContext(AppConfig appConfig) {
        this.appConfig = appConfig;
    }


    public AppConfig getAppConfig() {
        return this.appConfig;
    }

}
