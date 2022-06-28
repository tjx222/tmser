package com.tmser.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 当前时间获取修改， 配合javatime_ext 修改当前时间
 * 主要用于测试时间穿透
 *
 * @author tmser
 * @version 1.0
 */
@Slf4j
public class TimeModifyUtil {
    private static boolean canModifyTime = false;
    private static Method localDateTimeMethod;
    private static Method localDateMethod;
    private static Method localTimeMethod;

    static {
        try {
            String endorsedDir = System.getProperty("java.endorsed.dirs");
            log.info("TimeModifyUtil endorsedDir:{}", endorsedDir);
            if (StringUtils.isNotEmpty(endorsedDir)) {
                File file = new File(endorsedDir, "javatime_ext.jar");
                log.info("TimeModifyUtil file:{}", file.getAbsolutePath());
                if (file.exists()) {
                    localDateTimeMethod = LocalDateTime.class.getMethod("setLocalDateTime", long.class);
                    localDateMethod = LocalDate.class.getMethod("setLocalDateTime", long.class);
                    localTimeMethod = LocalTime.class.getMethod("setLocalDateTime", long.class);
                    canModifyTime = true;
                }
                log.info("TimeModifyUtil canModifyTime:{}", canModifyTime);
            }
        } catch (Exception e) {
            log.warn("TimeModifyUtil init ", e);
            localDateTimeMethod = null;
            localDateMethod = null;
            localTimeMethod = null;
        }
    }

    /**
     * 修改当前时间
     *
     * @param timestamp
     */
    public static void modifyNowTime(String timestamp) {
        if (!canModifyTime) {
            return;
        }
        try {
            if (StringUtils.isEmpty(timestamp)) {
                timestamp = "-1";
            }
            long time = Long.parseLong(timestamp);
            if (localDateTimeMethod != null) {
                localDateTimeMethod.invoke(null, time);
            }
            if (localDateMethod != null) {
                localDateMethod.invoke(null, time);
            }
            if (localTimeMethod != null) {
                localTimeMethod.invoke(null, time);
            }
            log.info("TimeModifyUtil modifyNowTime: {}", DateUtils.long2DateTime(time));
        } catch (Exception e) {
            log.error("modifyNowTime error time:{}", timestamp, e);
        }
    }

}
