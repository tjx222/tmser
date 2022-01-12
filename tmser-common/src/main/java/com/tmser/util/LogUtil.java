package com.tmser.util;

public final class LogUtil {
    public LogUtil() {
    }

    public static void log() {
        System.out.println("***************************************************");
    }

    public static void log(Object... args) {
        StringBuilder builder = new StringBuilder("* ");
        Object[] arr = args;
        int len$ = args.length;

        for (int i = 0; i < len$; ++i) {
            Object obj = arr[i];
            builder.append(obj);
        }

        System.out.println(builder);
    }
}
