package com.tmser.core.utils;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/**
 * json返回客户端工具类
 * 
 * @author tjx
 * @date 2014-2-17
 */
public class JsonResponseUtils {

	/**
	 * 直接将字Json的字符串写入到Response中
	 * 
	 * @param response
	 * @param jsonString
	 *            Json的字符串
	 */
	public static void responseJsonString(HttpServletResponse response, String jsonString) {
		PrintWriter pw = null;
		try {
			response.setContentType("text/javascript");
			response.setCharacterEncoding("utf-8");
			pw = response.getWriter();
			pw.write(jsonString);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
			pw = null;
		}
	}
}
