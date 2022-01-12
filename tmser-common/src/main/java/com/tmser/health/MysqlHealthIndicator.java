package com.tmser.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class MysqlHealthIndicator implements HealthIndicator {

    private  final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String SQL = "select 1";

    @Resource
    private DataSource dataSource;

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();
        try(Connection connection = dataSource.getConnection(); Statement s = connection.createStatement()){
           s.execute(SQL);
        }catch (Exception e){
            LOGGER.error("mysql HealthIndicator fail",e);
            return builder.down(e).build();
        }
        return builder.up().build();
    }
}