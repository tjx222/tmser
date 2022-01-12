package com.tmser.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cool.chen on 2017/8/20 17:06.
 * modify history:
 */
public class AppConfig {
    private final String organization;
    private final String parentName;
    private final String name;
    private final String token;
    private final String author;
    private final String department;
    private final String authorMobile;


    private final ServerManagement.AppServer server;
    private final Map<String, String> env;
    public AppConfig(String organization, String parentName, String name, String token, String author, String department, String authorMobile, ServerManagement.AppServer server, Map<String, String> env) {
        this.organization = organization;
        this.parentName = parentName;
        this.name = name;
        this.token = token;
        this.author = author;
        this.department = department;
        this.authorMobile = authorMobile;
        this.server = server;
        if(env == null) {
            this.env = Collections.emptyMap();
        } else {
            this.env = Collections.unmodifiableMap(new HashMap(env));
        }

    }

    public String getOrganization() {
        return this.organization;
    }

    public String getName() {
        return this.name;
    }

    public String getParentName() {
        return parentName;
    }

    public String getToken() {
        return this.token;
    }

    public String getAuthor() {
        return author;
    }

    public String getDepartment() {
        return department;
    }

    public String getAuthorMobile() {
        return authorMobile;
    }
    public ServerManagement.AppServer getServer() {
        return server;
    }

    public Map<String, String> getEnv() {
        return this.env;
    }

}
