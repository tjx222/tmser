package com.tmser.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

	private static String[] parsePatterns = { "yyyy-MM-dd",
			"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd",
			"yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" };

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 * @return 当前日期
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 * @return 当前日期
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}

	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 * 
	 * @param date 要格式话的日期
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
	 * @return 
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 * @return
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 * @return
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 * @return
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 * @return
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	  * @return
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}

	/**
	 * 日期型字符串转化为日期 格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" }
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
	 * @param date
	 * @return
	 */
	public static Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		// calendar.setFirstDayOfWeek(Calendar.SUNDAY);//每周从周日开始
		// calendar.setMinimalDaysInFirstWeek(1); // 设置每周最少为1天
		if(date != null) {
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
	 * @param end 结束日期
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
		return diff/(7*24*60*60*1000);
		
	}
	/** 
	* 获得指定日期的后一天 
	* @param specifiedDay 
	* @return 
	*/ 
	public static String getSpecifiedDayAfter(String specifiedDay){ 
		Calendar c = Calendar.getInstance(); 
		Date date=null; 
		try { 
			date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay); 
			c.setTime(date); 
		} catch (ParseException e) { 
			e.printStackTrace(); 
		} 
		int day=c.get(Calendar.DATE); 
		c.set(Calendar.DATE,day+1); 
	
		String dayAfter=new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()); 
		return dayAfter; 
	} 
}
