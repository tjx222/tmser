package com.tmser.blog.event.logger;

import org.springframework.context.ApplicationEvent;
import com.tmser.blog.model.enums.LogType;
import com.tmser.blog.model.params.LogParam;
import com.tmser.blog.utils.ServletUtils;
import com.tmser.blog.utils.ValidationUtils;

/**
 * @author johnniang
 * @date 19-4-20
 */
public class LogEvent extends ApplicationEvent {

    private final LogParam logParam;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param logParam login param
     */
    public LogEvent(Object source, LogParam logParam) {
        super(source);

        // Validate the log param
        ValidationUtils.validate(logParam);

        // Set ip address
        logParam.setIpAddress(ServletUtils.getRequestIp());

        this.logParam = logParam;
    }

    public LogEvent(Object source, String logKey, LogType logType, String content) {
        this(source, new LogParam(logKey, logType, content));
    }

    public LogParam getLogParam() {
        return logParam;
    }
}
