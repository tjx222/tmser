package com.tmser.blog.listener.logger;

import com.tmser.blog.event.logger.VisitLogEvent;
import com.tmser.blog.model.entity.VisitLog;
import com.tmser.blog.service.VisitLogService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Log event listener.
 *
 * @author johnniang
 * @date 19-4-21
 */
@Component
public class VisitLogEventListener {

    private final VisitLogService logService;

    public VisitLogEventListener(VisitLogService logService) {
        this.logService = logService;
    }

    @EventListener
    @Async
    public void onApplicationEvent(VisitLogEvent event) {
        // Convert to log
        VisitLog logToCreate = event.getVisitLogParam().convertTo();

        // Create log
        logService.create(logToCreate);
    }
}
