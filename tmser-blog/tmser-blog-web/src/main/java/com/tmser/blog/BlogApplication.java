package com.tmser.blog;

import com.google.common.base.Stopwatch;
import com.tmser.report.AppRunReport;
import com.tmser.report.AppRunReportLoader;
import com.tmser.util.AppUtils;
import com.tmser.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;
import java.util.concurrent.TimeUnit;

@ComponentScan({"com.tmser"})
@SpringBootApplication
public class BlogApplication {

    private static Logger log = LoggerFactory.getLogger(BlogApplication.class);

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String appName = AppUtils.appName;
        long elasp = 0L;
        try {
            log.info("========={}开始启动!========", appName);
            log.info("===================");
            log.info("===================");
            final ConfigurableApplicationContext app = SpringApplication.run(BlogApplication.class, args);
            stopwatch.stop();
            elasp = stopwatch.elapsed(TimeUnit.SECONDS);
            log.info("===================");
            log.info("===================");
            log.info("===================");
            log.info("========={}启动成功!====耗时{}秒====", appName, elasp);
            log.info("===================");
            log.info("===================");
            log.info("===================");
            send(null, elasp);
        } catch (Exception e) {
            log.info("====xxxxxxxxxxxxxxx======");
            log.info("====xxxxxxxxxxxxxxx======");
            log.info("====xxxxxxxxxxxxxxx======");
            log.error("{} 容器启动失败", appName, e);
            stopwatch.stop();
            elasp = stopwatch.elapsed(TimeUnit.SECONDS);
            send(e, elasp);
        }
    }

    private static void send(Exception e, long elasp) {
        List<AppRunReport> appRunReportList = AppRunReportLoader.getAppRunReportList();
        if (CollectionUtils.isNotEmpty(appRunReportList)) {
            for (AppRunReport appRunReport : appRunReportList) {
                appRunReport.report(elasp, e);
            }
        }
    }

}
