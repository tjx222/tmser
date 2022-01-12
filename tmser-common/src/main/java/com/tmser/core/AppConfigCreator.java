package com.tmser.core;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;


public class AppConfigCreator {
    public AppConfigCreator() {
    }

    public static AppConfig createNullAppConfig(ServerLocalContext localContext) {
        int pid = localContext.getPid();
        String localIp = localContext.getLocalIp();
        String logDir = localContext.getLogDir();
        return new AppConfig(null, null,null, null,null,null,null,new ServerManagement.AppServer(null, pid, null, null, null, localIp, 0, logDir, null), new HashMap());
    }

    public static AppConfig createLocalAppConfig(ServerLocalContext localContext) {
        ImmutableMap<String,String> appConfig = localContext.getAppConfig();
        String organization = appConfig.get("organization");
        String name = appConfig.get("name");
        String parentName = appConfig.get("parentName");
        String token = appConfig.get("token");
        String author = appConfig.get("author");
        String department = appConfig.get("department");
        String authorMobile = appConfig.get("authorMobile");
        int port = getIntOrDefault(appConfig, "server.port", 0);
        String hostname = appConfig.get("server.hostname");
        String room = appConfig.get("server.room");
        String serverToken = appConfig.get("server.token");
        int pid = localContext.getPid();
        String localIp = localContext.getLocalIp();
        String logDir = localContext.getLogDir();
        HashMap env = new HashMap(localContext.getEnvConfig());
        ServerManagement.AppServer.Type type = ServerManagement.AppServer.Type.valueOf(localContext.getEnv());
        return new AppConfig(organization,parentName, name, token,author,department,authorMobile, new ServerManagement.AppServer(name, pid, type, hostname, room, localIp, port, logDir, serverToken), env);
    }

    public static AppConfig createRemoteAppConfig(ServerLocalContext localContext) {
        ImmutableMap<String,String> appConfig = localContext.getAppConfig();
        String organization = appConfig.get("organization");
        String parentName = appConfig.get("parentName");
        String name = appConfig.get("name");
        String token = appConfig.get("token");
        String author = appConfig.get("author");
        String department = appConfig.get("department");
        String authorMobile = appConfig.get("authorMobile");
        int port = getIntOrDefault(appConfig, "server.port", 0);
        String hostname = appConfig.get("server.hostname");
        String room = appConfig.get("server.room");
        String serverToken = appConfig.get("server.token");
        HashMap env = new HashMap(localContext.getEnvConfig());
        ServerManagement.AppServer.Type type = typeOf(env);
        int pid = localContext.getPid();
        String localIp = localContext.getLocalIp();
        String logDir = localContext.getLogDir();
        return new AppConfig(organization, parentName,name, token,author,department,authorMobile, new ServerManagement.AppServer(name, pid, type, hostname, room, localIp, port, logDir, serverToken), env);
    }

    private static int getIntOrDefault(Map<String, String> map, String key, int def) {
        String value = map.get(key);

        try {
            return Integer.parseInt(value);
        } catch (Exception var5) {
            return def;
        }
    }
    private static ServerManagement.AppServer.Type typeOf(Map<String, String> env) {
        String name = env.get("name");
        if(Strings.isNullOrEmpty(name)) {
            name = System.getProperty("app.env");
        }

        if(Strings.isNullOrEmpty(name)) {
            name = System.getenv("app.env");
        }

        return Strings.isNullOrEmpty(name)?null: ServerManagement.AppServer.Type.valueOf(name);
    }
}
