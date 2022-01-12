package com.tmser.log.logback;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

public class SyncCallbackAppender extends AppenderBase<LoggingEvent> {

    private static List<OnAppendListener> onAppendListeners = new ArrayList<>();

    @Override
    protected void append(LoggingEvent eventObject) {
        onAppendListeners.forEach(item -> {
            item.onAppend(eventObject);
        });
    }

    public interface OnAppendListener {
        void onAppend(LoggingEvent eventObject);
    }

    public static void addAppendListenr(OnAppendListener listener) {
        if (listener != null && !onAppendListeners.contains(listener)) {
            onAppendListeners.add(listener);
        }

    }

    public static void removeAppendListenr(OnAppendListener listener) {
        if (listener != null && onAppendListeners.contains(listener)) {
            onAppendListeners.remove(listener);
        }
    }

}
