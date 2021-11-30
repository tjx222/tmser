package com.tmser.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期工具类, 继承org.apache.commons.lang3.time.DateUtils类
 *
 * @author tjx
 * @version 2013-12-30
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);
    public static final String DEFAULT_START_DATE = "1900-1-1";
    public static final String YMD = "yyyy-MM-dd";
    public static final String YMDHM = "yyyy-MM-dd HH:mm";
    public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String YMDHMSS = "yyyy-MM-dd HH:mm:ss:SS";
    public static final String USMMDDZZZYYYY = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final String HMS = "HH:mm:ss";
    public static final String HM = "HH:mm";
    public static final String YMDHMS_STR = "yyyyMMddHHmmss";
    public static final String YMDHMSSS_STR = "yyyyMMddHHmmssSSS";
    public static final String Y = "yyyy";
    public static final String M = "MM";
    public static final String D = "dd";
    public static final DateTimeFormatter defaultDateFormatter = DateTimeFormatter.ofPattern(YMD);
    public static final DateTimeFormatter defaultTimeFormatter = DateTimeFormatter.ofPattern(HMS);
    public static final DateTimeFormatter defaultDateTimeFormatter = DateTimeFormatter.ofPattern(YMDHMS);
    public static final DateTimeFormatter defaultDateTimeSimpleFormatter = DateTimeFormatter.ofPattern(YMDHMS_STR);
    public static final DateTimeFormatter defaultDateTimeSSSimpleFormatter = DateTimeFormatter.ofPattern(YMDHMSSS_STR);

    public static final DateTimeFormatter dateFormatterCn = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    public static final DateTimeFormatter dateTimeFormatterCn = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss秒");

    private static String[] parsePatterns = {YMD,YMDHMSS,YMDHMS,YMDHM,"yyyy/MM/dd",
            "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm"};


    private DateUtils() {
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     *
     * @param date    要格式话的日期
     * @param pattern 模式,默认"yyyy-MM-dd"
     * @return 格式化后的日期
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     *
     * @return
     */
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     *
     * @return
     */
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     *
     * @return
     */
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * 得到当前月份字符串 格式（MM）
     *
     * @return
     */
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    /**
     * 得到当天字符串 格式（dd）
     *
     * @return
     */
    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     *
     * @return
     */
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * 日期型字符串转化为日期 格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" }
     *
     * @param str
     * @return
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取过去的天数
     *
     * @param date
     * @return
     */
    public static long pastDays(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取 与今天相隔  num 天的 @see Date
     *
     * @param num
     * @return
     */
    public static Date nextDay(int num) {
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.DAY_OF_MONTH, curr.get(Calendar.DAY_OF_MONTH) + num);
        return curr.getTime();
    }

    /**
     * 获取 与今天相隔  num 月的 @see Date
     *
     * @param num
     * @return
     */
    public static Date nextMonth(int num) {
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.MONTH, curr.get(Calendar.MONTH) + num);
        return curr.getTime();
    }

    /**
     * 获取 与今天相隔  num 年的 @see Date
     *
     * @param num
     * @return
     */
    public static Date nextYear(int num) {
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.YEAR, curr.get(Calendar.YEAR) + num);
        return curr.getTime();
    }

    /**
     * 获取指定日期的@see Calendar
     *
     * @param date
     * @return
     */
    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        // calendar.setFirstDayOfWeek(Calendar.SUNDAY);//每周从周日开始
        // calendar.setMinimalDaysInFirstWeek(1); // 设置每周最少为1天
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar;
    }

    /**
     * 计算两个日期之间相差多少天
     *
     * @param start
     * @param end
     * @return
     * @throws ParseException
     */
    public static long getDaysBetween(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        return diff / (24l * 60l * 60l * 1000l);
    }

    /**
     * 计算两个日期之间相差多少周
     *
     * @param start 起始日期
     * @param end   结束日期
     * @return 周数
     */
    public static long getWeeksBetween(Date start, Date end) {
        //归零开始时间的时分秒、毫秒
        Calendar calendar_start = getCalendar(start);
        calendar_start.set(Calendar.HOUR_OF_DAY, 0);
        calendar_start.set(Calendar.MINUTE, 0);
        calendar_start.set(Calendar.SECOND, 0);
        calendar_start.setFirstDayOfWeek(Calendar.SUNDAY);

        //归零结束时间的时分秒、毫秒
        Calendar calendar_end = getCalendar(end);
        calendar_end.set(Calendar.HOUR_OF_DAY, 0);
        calendar_end.set(Calendar.MINUTE, 0);
        calendar_end.set(Calendar.SECOND, 0);
        calendar_end.setFirstDayOfWeek(Calendar.SUNDAY);

        calendar_start.set(Calendar.DAY_OF_WEEK, calendar_start.getFirstDayOfWeek());
        log.debug("开始时间===" + formatDate(calendar_start.getTime(), "yyyy-MM-dd HH:mm:ss"));

        calendar_end.set(Calendar.DAY_OF_WEEK, calendar_end.getFirstDayOfWeek());
        log.debug("结束时间===" + formatDate(calendar_end.getTime(), "yyyy-MM-dd HH:mm:ss"));

        long diff = calendar_end.getTimeInMillis() - calendar_start.getTimeInMillis();
        return diff / (7 * 24 * 60 * 60 * 1000);

    }

    /**
     * 获得指定日期的后一天
     *
     * @param specifiedDay
     * @return
     */
    public static String getSpecifiedDayAfter(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
            c.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);

        String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayAfter;
    }




    /*
     * Date --> String
     */

    /**
     * @param date       日期类型
     * @param dateFormat 日期格式
     * @return 格式化字符串
     */
    public static String date2String(LocalDate date, DateTimeFormatter dateFormat) {
        if (date == null)
            return null;
        return dateFormat.format(date);
    }

    /**
     * @param date       日期类型
     * @param dateFormat 日期格式
     * @return 格式化字符串
     */
    public static String date2String(LocalDate date, String dateFormat) {
        if (date == null)
            return null;
        return date2String(date, DateTimeFormatter.ofPattern(dateFormat));
    }

    /**
     * @param date 日期类型
     * @return 格式化字符串
     */
    public static String date2String(LocalDate date) {
        if (date == null)
            return null;
        return date2String(date, defaultDateFormatter);
    }

    /**
     * @param time       时间类型
     * @param dateFormat 时间格式
     * @return 格式化的字符串
     */
    public static String time2String(LocalTime time, DateTimeFormatter dateFormat) {
        if (time == null)
            return null;
        return dateFormat.format(time);
    }

    public static String time2String(LocalTime time, String dateFormat) {
        if (time == null)
            return null;
        return time2String(time, DateTimeFormatter.ofPattern(dateFormat));
    }

    public static String time2String(LocalTime time) {
        if (time == null)
            return null;
        return time2String(time, defaultTimeFormatter);
    }

    /**
     * @param dateTime   日期时间
     * @param dateFormat 格式
     * @return 格式化字符串
     */
    public static String dateTime2String(LocalDateTime dateTime, DateTimeFormatter dateFormat) {
        if (dateTime == null)
            return null;
        return dateFormat.format(dateTime);
    }

    /**
     * @param dateTime   日期时间
     * @param dateFormat 格式
     * @return 格式化字符串
     */
    public static String dateTime2String(LocalDateTime dateTime, String dateFormat) {
        if (dateTime == null)
            return null;
        return dateTime2String(dateTime, DateTimeFormatter.ofPattern(dateFormat));
    }

    /**
     * @param dateTime 日期时间
     * @return 格式化字符串
     */
    public static String dateTime2String(LocalDateTime dateTime) {
        if (dateTime == null)
            return null;
        return dateTime2String(dateTime, defaultDateTimeFormatter);
    }

    public static LocalDateTime newDateTime(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
    }
    /*
     * String -->Date
     */

    public static LocalDate string2Date(String date, DateTimeFormatter dateFormat) {
        if (date == null) {
            return null;
        }
        return LocalDate.parse(date, dateFormat);
    }

    public static LocalDate newDate(int year, int month, int dayOfMonth) {
        return LocalDate.of(year, month, dayOfMonth);
    }

    public static LocalDate string2Date(String date, String dateFormat) {
        return string2Date(date, DateTimeFormatter.ofPattern(dateFormat));
    }

    public static LocalDate string2Date(String date) {
        return string2Date(date, defaultDateFormatter);
    }

    public static LocalTime newTime(int hour, int minute, int second) {
        return LocalTime.of(hour, minute, second);
    }

    public static LocalTime string2Time(String time, DateTimeFormatter timeFormat) {
        if (time == null)
            return null;
        return LocalTime.parse(time, timeFormat);
    }

    public static LocalTime string2Time(String time, String timeFormat) {
        if (time == null)
            return null;
        return LocalTime.parse(time, DateTimeFormatter.ofPattern(timeFormat));
    }

    public static LocalTime string2Time(String time) {
        return string2Time(time, defaultTimeFormatter);
    }

    public static LocalDateTime string2DateTime(String dateTime, DateTimeFormatter timeFormat) {
        if (dateTime == null)
            return null;
        return LocalDateTime.parse(dateTime, timeFormat);
    }

    public static LocalDateTime string2DateTime(String dateTime, String dateTimeFormat) {
        if (dateTime == null)
            return null;
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(dateTimeFormat));
    }

    public static LocalDateTime string2DateTime(String dateTime) {
        return string2DateTime(dateTime, defaultDateTimeFormatter);
    }

    /**
     * 取得当前日期。日期格式为：yyyy-MM-dd
     *
     * @return 当前日期字符串。
     */
    public static String getCurrentDateAsString() {
        return LocalDate.now().toString();
    }

    /**
     * 取得当前日期时间。日期格式为：uuuu-MM-dd'T'HH:mm:ss.SSS
     *
     * @return 当前日期字符串。
     */
    public static String getCurrentDateTimeAsString() {
        return LocalDateTime.now().toString();
    }

    /**
     * 取得当前日期时间。日期格式为：yyyyMMddhhmmss *
     *
     * @return 当前日期字符串。
     */
    public static String getCurrentDateTimeAsLong() {
        return LocalDateTime.now().format(defaultDateTimeSimpleFormatter);
    }

    /**
     * 取得当前日期时间。日期格式为：yyyyMMddhhmmssSS *
     *
     * @return 当前日期字符串。
     */
    public static String getCurrentDateTimeSSSAsLong() {
        return LocalDateTime.now().format(defaultDateTimeSSSimpleFormatter);
    }

    /**
     * 取得当前日期时间。日期格式为由dateFormat定义
     *
     * @param dateFormat 格式串
     * @return 当前日期字符串。
     */
    public static String getCurrentDateAsString(String dateFormat) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(dateFormat));
    }

    public static String getCurrentDateTimeAsString(String dateFormat) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateFormat));
    }

    /**
     * 时间戳转换成日期时间
     *
     * @param milliseconds
     * @return
     */
    public static LocalDateTime long2DateTime(long milliseconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
    }

    public static LocalDateTime longSecond2DateTime(long seconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneId.systemDefault());
    }

    /**
     * 日期时间转换成时间戳
     *
     * @param localDateTime
     * @return
     */
    public static long dateTime2Long(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime).getTime();
    }

    /**
     * 取得当前的时间戳
     * for:  2017-08-19T16:03:43.387
     *
     * @return 时间戳
     */
    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 取得当前的时间戳 1503129823387
     *
     * @return 时间戳
     */
    public static long now() {
        return dateTime2long(LocalDateTime.now());
    }


    /**
     * 验证字符串是否为合法日期格式 支持YYYY-MM-DD OR YYYY-MM-DD HH:mm:ss
     *
     * @param dateString
     */
    public static boolean validateDateFormat(String dateString) {
        Boolean validate = Boolean.FALSE;
        String reg1 = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-9]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        String reg2 = "^((\\d{2}(([02468][048])|([13579][26]))" + "[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|"
                + "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?" + "((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?("
                + "(((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?"
                + "((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";

        Pattern p1 = Pattern.compile(reg1);
        Pattern p2 = Pattern.compile(reg2);
        Matcher m1 = p1.matcher(dateString);
        Matcher m2 = p2.matcher(dateString);
        if (m1.matches() || m2.matches()) {
            validate = Boolean.TRUE;
        }
        return validate;
    }

    public static Duration getDuration(LocalDateTime d1, LocalDateTime d2) {
        return Duration.between(d1, d2);
    }

    /**
     * description:计算两个时间差
     *
     * @param d1
     * @param d2
     * @return 返回以秒为单位
     */
    public static long decrease(LocalDateTime d1, LocalDateTime d2) {
        return Duration.between(d1, d2).getSeconds();
    }

    /**
     * description:计算两个时间差
     *
     * @param d1
     * @param d2
     * @return 返回以毫秒为单位
     */
    public static long decreaseMilli(LocalDateTime d1, LocalDateTime d2) {
        return Duration.between(d1, d2).toMillis();
    }

    public static long dateTime2long(LocalDateTime d1) {
        return d1.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取最小的
     *
     * @param localDates
     * @return
     */
    public static LocalDate min(LocalDate... localDates) {
        if (localDates == null || localDates.length == 0) {
            return null;
        }
        List<LocalDate> localDateList = Arrays.asList(localDates);
        Collections.sort(localDateList, Comparator.naturalOrder());
        return localDateList.get(0);
    }

    /**
     * 获取最大的
     *
     * @param localDates
     * @return
     */
    public static LocalDate max(LocalDate... localDates) {
        if (localDates == null || localDates.length == 0) {
            return null;
        }
        List<LocalDate> localDateList = Arrays.asList(localDates);
        Collections.sort(localDateList, Comparator.reverseOrder());
        return localDateList.get(0);
    }
}
