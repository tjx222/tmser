package com.tmser.log.logback;


import ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import com.tmser.util.AppUtils;
import com.tmser.util.DateUtils;
import com.tmser.util.JsonUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class MyLogLayout extends LayoutBase<ILoggingEvent> {


    @Override
    public String doLayout(ILoggingEvent event) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("P", event.getLoggerContextVO().getName());
        map.put("HOST", AppUtils.getLocalHostName());
        String requestId = event.getMDCPropertyMap().get("request-id") != null ? event.getMDCPropertyMap().get("request-id") : event.getMDCPropertyMap().get("requestId");
        map.put("ReqId", requestId);
        String traceId = event.getMDCPropertyMap().get("traceId");
        map.put("TraceId", traceId);
        map.put("T", DateUtils.getCurrentDateTimeAsString("yyyy-MM-dd HH:mm:ss.SSS"));
        map.put("L", event.getLevel());
        map.put("THREAD", event.getThreadName());
        StackTraceElement[] cda = event.getCallerData();
        String line = cda != null && cda.length > 0 ? Integer.toString(cda[0].getLineNumber()) : "?";
        map.put("CLASS", event.getLoggerName() + ":" + line);
        String message = event.getFormattedMessage();
        if (event.getThrowableProxy() != null) {
            ExtendedThrowableProxyConverter throwableConverter = new ExtendedThrowableProxyConverter();
            throwableConverter.start();
            message = event.getFormattedMessage() + "\n" + throwableConverter.convert(event);
            throwableConverter.stop();
        }
        message = message.replaceAll("\\n", "");
        map.put("IZKM", message);

        return JsonUtil.toJson(map) + CoreConstants.LINE_SEPARATOR;
    }

}