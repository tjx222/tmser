package com.tmser.common.config;

import com.tmser.spring.web.PageableHandlerMethodArgumentResolver;
import com.tmser.spring.web.SortHandlerMethodArgumentResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 *
 */
@Configuration
@Slf4j
public class AppMvcConfigurer implements WebMvcConfigurer {

    // 参数解析器
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        SortHandlerMethodArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
        PageableHandlerMethodArgumentResolver pageableResolver = //
                new PageableHandlerMethodArgumentResolver(sortResolver);
        argumentResolvers.add(sortResolver);
        argumentResolvers.add(pageableResolver);
    }

}
