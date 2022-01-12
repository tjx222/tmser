package com.tmser.core;

import com.google.common.collect.ImmutableMap;


public class ServerContextInitializer {
    private final ServerContext context;

    public ServerContextInitializer() {
        ServerLocalContext localContext = (new ServerLocalContextLoader()).loadLocalContext();
        AppConfig config = AppConfigLoaders.getLoader(localContext).loadAppConfig(localContext);
        this.context = new ServerContext(config);
        ImmutableMap izkEnv = localContext.getEnvConfig();
        (new GlobalEnvWriter(izkEnv)).writeToSystem();

        ImmutableMap globalEnv = localContext.getAppConfig();
        (new GlobalEnvWriter(globalEnv)).writeToSystem();
}

    public ServerContext getContext() {
        return this.context;
    }
}
