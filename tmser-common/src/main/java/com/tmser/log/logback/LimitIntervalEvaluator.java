package com.tmser.log.logback;

import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.spi.ContextAwareBase;
import com.tmser.util.DateUtils;

public class LimitIntervalEvaluator extends ContextAwareBase implements EventEvaluator {
    private long lastSend = 0;
    private long interval = 0;
    private String name;

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    @Override
    public boolean evaluate(Object event) throws NullPointerException, EvaluationException {
        long now = DateUtils.now();
        if (now - lastSend > interval) {
            lastSend = now;
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }
}
