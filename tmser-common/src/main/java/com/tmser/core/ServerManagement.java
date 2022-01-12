package com.tmser.core;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public interface ServerManagement {


    void addServiceRecycle(ServiceRecycle var1);

    AppConfig getAppConfig();

    /**
     * Created by cool.chen on 2017/11/13 12:36.
     * modify history:
     */

    final class AppServer implements Serializable {
        private static final long serialVersionUID = 4032737575155531528L;
        private final String app;
        private final int pid;
        private final Type type;
        private final String hostname;
        private final String room;
        private final String ip;
        private int port;
        private final String logdir;
        private final String token;

        public AppServer(String app, int pid, Type type, String hostname, String room, String ip, int port, String logdir, String token) {
            this.app = app;
            this.pid = pid;
            this.type = type;
            this.hostname = hostname;
            this.room = room;
            this.ip = ip;
            this.port = port;
            this.logdir = logdir;
            this.token = token;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getApp() {
            return this.app;
        }

        public int getPid() {
            return this.pid;
        }

        public Type getType() {
            return this.type;
        }

        public String getHostname() {
            return this.hostname == null ? this.ip : this.hostname;
        }

        public String getRoom() {
            return this.room;
        }

        public String getIp() {
            return this.ip;
        }

        public int getPort() {
            return this.port;
        }

        public String getToken() {
            return this.token;
        }

        public String getLogdir() {
            return this.logdir;
        }


        public static String hostnameOf(String ip) {
            try {
                return InetAddress.getByName(ip).getHostName();
            } catch (UnknownHostException var2) {
                return ip;
            }
        }

        public enum Type {
            local,
            dev,
            dev_local,
            beta,
            beta_local,
            pre,
            prod,
            master,
            docker;

            private Type() {
            }
        }
    }
}
