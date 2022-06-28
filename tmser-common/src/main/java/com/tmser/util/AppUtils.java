package com.tmser.util;

import com.tmser.core.ServerManager;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public abstract class AppUtils {

    public static final String appName;
    public static final String appParentName;
    public static final String runEvn;
    public static final String author;
    public static final String department;
    private static final String authorMobile;
    private static String localHostAddress;
    private static String localHostName;

    public static String getAppName() {
        return appName;
    }

    public static String getRunEvn() {
        return runEvn;
    }

    public static String getAuthor() {
        return author;
    }

    public static String getAuthorMobile() {
        return authorMobile;
    }

    public static String getLocalHostName() {
        return localHostName;
    }

    public static String getLocalHostAddress() {
        return localHostAddress;
    }

    public static String getDepartment() {
        return department;
    }

    public static String getAppParentName() {
        return appParentName;
    }

    private static int getPid() {
        byte pid = 0;

        try {
            RuntimeMXBean ignored = ManagementFactory.getRuntimeMXBean();
            String name = ignored.getName();
            return Integer.parseInt(name.substring(0, name.indexOf(64)));
        } catch (Throwable var3) {
            return pid;
        }
    }

    public static boolean getBoolean(String str, boolean def) {
        if (str == null || str.trim().length() == 0) {
            return def;
        } else {
            str = str.trim().toUpperCase();
            return "TRUE".equals(str) || "YES".equals(str) || "ON".equals(str) || "1".equals(str);
        }
    }

    public static int getInt(String str, int def) {
        if (str == null) {
            return def;
        } else {
            try {
                return Integer.parseInt(str.trim());
            } catch (NumberFormatException var3) {
                return def;
            }
        }
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    static {
        try {
            localHostAddress = IpUtils.getLocalAddress().getHostAddress();
        } catch (NullPointerException var2) {
            localHostAddress = "";
        }

        try {
            localHostName = IpUtils.getLocalAddress().getHostName();
        } catch (NullPointerException var1) {
            localHostName = "";
        }
        appParentName = ServerManager.Tool.getAppConfig().getParentName();
        appName = ServerManager.Tool.getAppConfig().getName();
        runEvn = ServerManager.Tool.getAppConfig().getServer().getType().name();
        authorMobile = ServerManager.Tool.getAppConfig().getAuthorMobile();
        author = ServerManager.Tool.getAppConfig().getAuthor();
        department = ServerManager.Tool.getAppConfig().getDepartment();
    }
}
