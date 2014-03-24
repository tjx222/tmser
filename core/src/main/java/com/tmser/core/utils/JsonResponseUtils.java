package com.tmser.core.utils;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json返回客户端工具类
 * 
 * @author 张凯
 * @date 2014-2-17
 */
public class JsonResponseUtils {

	/**
	 * 返回Object对象到客户端
	 * @param obj
	 */
	public static void writeObject(Object obj) {
		responseJsonString(objToJsonString(obj));
	}
	
	/**
	 * 返回String到客户端
	 * @param str
	 */
	public static void writeString(String str) {
		responseJsonString(str);
	}
	
	/**
	 * Object对象转换为Json格式字符串
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String objToJsonString(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		String result = null;
		try {
			result = mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	

	/**
	 * 直接将Json的串发送给客户端
	 * 
	 * @param jsonString
	 *            json的串
	 */
	public static void responseJsonString(String jsonString) {
		HttpServletResponse response = ServletActionContext.getResponse();
		responseJsonString(response, jsonString);
	}

	/**
	 * 直接将字Json的字符串写入到Response中
	 * 
	 * @param response
	 * @param jsonString
	 *            Json的字符串
	 */
	private static void responseJsonString(HttpServletResponse response, String jsonString) {
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
