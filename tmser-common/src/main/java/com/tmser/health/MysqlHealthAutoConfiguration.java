package com.tmser.health;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(HealthIndicator.class)
public class MysqlHealthAutoConfiguration {
    @Bean
    public MysqlHealthIndicator mysqlHealthIndicator() {
        return new MysqlHealthIndicator();
    }

}
