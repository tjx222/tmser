package com.tmser.report;

public interface AppRunReport {

    /**
     * app 启动结果报告
     *
     * @param elapsed 耗时
     */
    void report(long elapsed);

    /**
     *
     * 发布告警
     *
     * @param error 启动过程是否有异常
     */
    void report(long elapsed, Throwable error);
}
