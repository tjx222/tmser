package com.tmser.report;

public class DingTalkAppRunReport {

    /**
     * 发布告警
     *
     * @param error 启动过程是否有异常
     *
    public static void send(Throwable error, long elapsed) {
        try {
            //开发人员本机调试
            if (DeveloperUtil.isLocalDebug()) {
                return;
            }
            ISendAlarm yachAlarm = new YachAlarm(Util.appName+"启动", ConfigUtils.get("alarm","appRunAccessToken"),ConfigUtils.get("alarm","appRunSecret"),null);
            String commitInfo = ChangeLogHelper.readChangeLogSummary();
            if (StringUtil.isNotEmpty(commitInfo) && commitInfo.contains("changelog.txt")){
                commitInfo = commitInfo + "(请开发联系运维配置txt文件)";
            }
            String markDownMsg = StringUtil.format("服务名称: {0}\n机器地址: {1}\n机器名称: {2}\n启动时间: {3}\n启动耗时: {4}秒\n{5}",
                    Util.appName,
                    IpUtil.getLocalIp(),
                    IpUtil.getHostName(),
                    DateUtil.getCurrentDateTimeAsString(),
                    elapsed,
                    commitInfo);

            boolean result;
            if (error == null) {
                result = yachAlarm.send(("启动状态: 成功\n" + markDownMsg).replace("\n", "\n\n"), Arrays.asList(Util.getAuthorMobile()));
            } else {
                String message = error.getMessage();
                int errorlength  = message !=null ? message.length() :0;
                if (errorlength >2700){
                    message = message.substring(0,2700);
                }
                result = yachAlarm.send(("<font color='#ff0000'>启动状态: 异常</font>\n" + markDownMsg + "\n<font color='#ff0000'>异常描述: " + message + "</font>").replace("\n", "\n\n"), Collections
                        .singletonList(Util.getAuthorMobile()));
            }
            log.info("发送yach报警结果: {}", result);
        } catch (Exception e) {
            log.info("发送yach报警异常!", e);
        }
    }
    */
}
