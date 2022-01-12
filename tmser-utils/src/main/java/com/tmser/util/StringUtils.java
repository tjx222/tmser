package com.tmser.utils;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 * @author tjx
 * @version 2013-12-30
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
	public static final String COMMA= ",";

	/**
	 * 替换掉HTML标签方法
	 * @param html
	 * @return
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html)){
			return "";
		}
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 * @param str 目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String abbr(String str, int length,boolean needEllipsis,boolean needEncoding) {
		if (str == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : str.toCharArray()) {
				currentLength += String.valueOf(c).getBytes("GBK").length;
				if (currentLength <= length - (needEllipsis ? 3 : 0)) {
					sb.append(c);
				} else if(needEllipsis){
					sb.append("...");
					break;
				}
			}
			if(needEncoding){
				return URLEncoder.encode(sb.toString(), "utf-8");
			}
			
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 转换为Double类型
	 * 
	 * @param val
	 * @return
	 */
	public static Double toDouble(Object val){
		if (val == null){
			return 0D;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 * @param val
	 * @return
	 */
	public static Float toFloat(Object val){
		return toDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 * @param val
	 * @return
	 */
	public static Long toLong(Object val){
		return toDouble(val).longValue();
	}

	/**
	 * 转换为Integer类型
	 * @param val
	 * @return
	 */
	public static Integer toInteger(Object val){
		return toLong(val).intValue();
	}
	
	/**
	 * 字符串转换为Integer数组
	 * @param val 字符串
	 * @param regex 正则表达式
	 * @return
	 */
	public static Integer[] toIntegerArray(String val, String regex) {
		String[] valArr = val.split(regex);
		int arrLen = valArr.length;
		Integer[] it = new Integer[arrLen];
		for(int i=0; i < arrLen; i++) {
			it[i] = Integer.parseInt(valArr[i].trim());
		}
		return it;
	}
	
	/**
	 * 数组转成字符串
	 * <p>
	 * 可在打印日志的时候用
	 * </p>
	 * 
	 * @param args
	 * @return
	 */
	public static String argsToString(Object[] args){
		StringBuilder s = new StringBuilder("args:[");
		for(Object o : args){
			s.append(o).append(",");
		}
		s.append("]");
		
		return s.toString();
	}
	/**
	 * null 转换为空字符
	 * @param o
	 * @return
	 */
	public static String nullToEmpty(Object o){
		return o == null ? "" : o.toString();
	}
	
	/**
	 * 分割字符串，并转换为整形列表
	 * @param source
	 * @param regex
	 * @return
	 */
	public static List<Integer> splitToIntegerList(String source,String regex){
		 List<Integer> rs = new ArrayList<Integer>();
		 if(isNotEmpty(source)){
			 String[] arr = split(source,regex);
			 for(String a : arr){
				 if(isNotEmpty(a)){
					 rs.add(Integer.valueOf(a));
				 }
			 }
		 }
		return rs;
	}
	
	/**
	 *  使用","分割字符串，并转换为整形列表
	 * @param source
	 * @return
	 */
	public static List<Integer> splitToIntegerList(String source){
		return splitToIntegerList(source,COMMA);
	}
}
