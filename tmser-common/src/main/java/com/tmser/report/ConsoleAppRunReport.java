package com.tmser.report;

import com.tmser.log.commitlog.ChangeLogHelper;
import com.tmser.util.AppUtils;
import com.tmser.util.DateUtils;
import com.tmser.util.IpUtils;
import com.tmser.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleAppRunReport implements AppRunReport {

    /**
     * 发布告警
     *
     * @param error 启动过程是否有异常
     */
    public void report(long elapsed, Throwable error) {
        String commitInfo = ChangeLogHelper.readChangeLogSummary();
        if (StringUtils.isNotEmpty(commitInfo) && commitInfo.contains("changelog.txt")) {
            commitInfo = commitInfo + "(请开发联系运维配置txt文件)";
        }
        String markDownMsg = StringUtils.format("服务名称: {0}\n机器地址: {1}\n机器名称: {2}\n启动时间: {3}\n启动耗时: {4}秒\n{5}",
                AppUtils.appName,
                IpUtils.getLocalIp(),
                IpUtils.getHostName(),
                DateUtils.getCurrentDateTimeAsString(),
                elapsed,
                commitInfo);

        boolean result;
        if (error == null) {
            System.out.println(("启动状态: 成功\n" + markDownMsg));
        } else {
            String message = error.getMessage();
            int errorlength = message != null ? message.length() : 0;
            if (errorlength > 2700) {
                message = message.substring(0, 2700);
            }
            System.out.println(("<font color='#ff0000'>启动状态: 异常</font>\n" + markDownMsg + "\n<font color='#ff0000'>异常描述: " + message + "</font>").replace("\n", "\n\n"));
        }
    }

    public void report(long elapsed) {
        report(elapsed, null);
    }
}

