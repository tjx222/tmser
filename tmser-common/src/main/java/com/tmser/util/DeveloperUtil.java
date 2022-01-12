package com.tmser.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class DeveloperUtil {

    private static final Logger logger = LoggerFactory.getLogger(DeveloperUtil.class);

    public static String getEnv(String key, String defaultValue) {
        String env = System.getProperty(key);
        if (env == null) {
            env = System.getenv(key);
        }

        if (env == null) {
            env = defaultValue;
        }

        return env;
    }

    /**
     * 是否是开发人员本机调试
     *
     * @return
     */
    public static boolean isLocalDebug() {
        try {
            String env = getEnv("run_env", null);
            if(Objects.nonNull(env)){
                return env.toLowerCase().contains("local");
            }
            String osName = getEnv("os.name",null); //操作系统名称
            if (null != osName ) {
                if (osName.toLowerCase().contains("mac") || osName.toLowerCase().contains("windows")) {
                    //操作系统是mac或者windows的 认为是开发自己调试
                    return true;
                }
            }
        } catch (Exception ex) {
            logger.error("isLocalDebug", ex);
        }
        return false;
    }
}
