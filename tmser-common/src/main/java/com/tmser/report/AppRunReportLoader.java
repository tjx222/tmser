package com.tmser.report;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * SPI load app run report
 */
public class AppRunReportLoader {

    private static final List<AppRunReport> appRunReportList;

     static {
        List<AppRunReport> appRunReports = Lists.newArrayList();
        ServiceLoader<AppRunReport> serviceLoader = ServiceLoader.load(AppRunReport.class);
        for (AppRunReport appRunReport: serviceLoader){
            appRunReports.add(appRunReport);
        }

        if(appRunReports.size() == 0){
            appRunReports.add(new ConsoleAppRunReport());
        }
         appRunReportList = Collections.unmodifiableList(appRunReports);

    }


    public static List<AppRunReport> getAppRunReportList(){
         return appRunReportList;
    }
}
