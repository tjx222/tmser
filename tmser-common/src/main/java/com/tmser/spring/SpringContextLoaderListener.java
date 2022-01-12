package com.tmser.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;

/**
 * @Title SpringContextLoaderListener
 * @Description 为了能对spring上下文有更大的操作权，通过这个类在容器初始化时就获得servletContext
 * @ModifiedHistory
 */
public class SpringContextLoaderListener extends ContextLoaderListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(SpringContextLoaderListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        LOGGER.info("初始化spring容器开始");
        super.contextInitialized(event);
        SpringApplicationContext.setAppContext(WebApplicationContextUtils
                .getWebApplicationContext(event.getServletContext()));
        LOGGER.info("初始化spring容器结束");
    }

}
