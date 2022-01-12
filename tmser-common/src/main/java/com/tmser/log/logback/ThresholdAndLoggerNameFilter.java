package com.tmser.log.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;

public class ThresholdAndLoggerNameFilter extends Filter<ILoggingEvent> {
    private Level level;
    private String loggerPrefix;

    public ThresholdAndLoggerNameFilter() {
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!this.isStarted()) {
            return FilterReply.NEUTRAL;
        } else if (!event.getLevel().isGreaterOrEqual(this.level)) {
            return FilterReply.DENY;
        } else if (StringUtils.isBlank(this.loggerPrefix)) {
            return FilterReply.NEUTRAL;
        } else {
            Iterable words = Splitter.on(",").split(this.loggerPrefix);
            Iterator i$ = words.iterator();
            String filterMsg;
            do {
                if (!i$.hasNext()) {
                    return FilterReply.NEUTRAL;
                }
                filterMsg = (String) i$.next();
            } while (!StringUtils.startsWithIgnoreCase(event.getLoggerName(), filterMsg));

            return FilterReply.DENY;
        }
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setLoggerPrefix(String loggerPrefix) {
        this.loggerPrefix = loggerPrefix;
    }

    public void start() {
        if (this.level != null) {
            super.start();
        }
    }
}
