package com.tmser.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmser.spring.SpringApplicationContext;
import com.tmser.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AppAutoConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonUtil.getDefaultMapper();
    }


    @Bean
    public SpringApplicationContext springApplicationContext() {
        return new SpringApplicationContext();
    }


}
