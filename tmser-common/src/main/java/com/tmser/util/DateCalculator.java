package com.tmser.util;


import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;

/**
 * 日期计算工具方法
 * @author tmser
 * @version 1.0
 * @title 日期计算工具类
 */
public class DateCalculator {

    /**
     * 获取date所在周的周一
     *
     * @param date 指定的时间
     * @return
     */
    public static LocalDate getMonday(LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - 1);
    }


    /**
     * 获取当前时间n周后的周一
     *
     * @param date
     * @param week
     * @return
     */
    public static LocalDate getMonday(LocalDate date, int week) {
        LocalDate monday = getMonday(date);
        return monday.plusDays(week * 7);
    }

    /**
     * 获取data所在周的周日
     *
     * @param date
     * @return
     */
    public static LocalDate getSunday(LocalDate date) {
        return getMonday(date).plusDays(6);
    }

    /**
     * 获取date时间n周后的周日
     *
     * @param date
     * @param week
     * @return
     */
    public static LocalDate getSunday(LocalDate date, int week) {
        return getSunday(date).plusDays(week * 7);
    }

    /**
     * 根据起始时间按照周拆分
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<LocalDate> getWeeks(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> list = new ArrayList<LocalDate>();
        if (startDate.compareTo(endDate) <= 0) {
            int week = 0;
            LocalDate monday = null;
            while ((monday = DateCalculator.getMonday(startDate, week)).compareTo(endDate) <= 0) {
                LocalDate sunday = DateCalculator.getSunday(startDate, week);
                list.add(monday);
                list.add(sunday);
                week++;
            }
        }
        return list;
    }

    /**
     * 获取2个日期之间所有天
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<LocalDate> getBetweenDays(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> list = new ArrayList<LocalDate>();
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days >= 0) {
            for (long i = 0; i <= days; i++) {
                LocalDate localDate = startDate.plusDays(i);
                list.add(localDate);
            }
        } else {
            for (long i = days; i <= 0; i++) {
                LocalDate localDate = endDate.minusDays(i);
                list.add(localDate);
            }
        }
        return list;
    }

    /**
     * 获取指定日期是周几
     *
     * @param date
     * @return 中文字符串
     */
    public static String getStringWeekDay(LocalDate date) {
        return getStringWeekDay(getIntWeekDay(date));
    }

    /**
     * @param dayofWeek 周几 数字类型
     * @return 中文字符串
     */
    private static String[] WEEKDAY_CH = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    public static String getStringWeekDay(int dayofWeek) {
        ValueRange.of(1,7).checkValidIntValue(dayofWeek, ChronoField.DAY_OF_WEEK);
        return WEEKDAY_CH[dayofWeek - 1];
    }

    /**
     * 获取指定日期是周几
     *
     * @param date
     * @return 返回数字
     */
    public static Integer getIntWeekDay(LocalDate date) {
        return date.getDayOfWeek().getValue();
    }


}
