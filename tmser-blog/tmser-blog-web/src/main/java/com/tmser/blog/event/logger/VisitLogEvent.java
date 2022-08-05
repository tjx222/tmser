package com.tmser.blog.event.logger;

import com.tmser.blog.model.enums.LogType;
import com.tmser.blog.model.params.LogParam;
import com.tmser.blog.model.params.VisitLogParam;
import com.tmser.blog.utils.ServletUtils;
import com.tmser.blog.utils.ValidationUtils;
import org.springframework.context.ApplicationEvent;

import javax.persistence.criteria.CriteriaBuilder;

/**
 * visitLog
 * @author johnniang
 * @date 19-4-20
 */
public class VisitLogEvent extends ApplicationEvent {

    private final VisitLogParam visitLogParam;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param visitLogParam login param
     */
    public VisitLogEvent(Object source, VisitLogParam visitLogParam) {
        super(source);

        // Validate the log param
        ValidationUtils.validate(visitLogParam);

        // Set ip address
        visitLogParam.setIpAddress(ServletUtils.getRequestIp());

        this.visitLogParam = visitLogParam;
    }

    public VisitLogEvent(Object source, Integer shareId, Integer contentId, String shareName, String contentName) {
        this(source, new VisitLogParam(shareId, contentId, shareName,contentName));
    }

    public VisitLogParam getVisitLogParam() {
        return visitLogParam;
    }
}
