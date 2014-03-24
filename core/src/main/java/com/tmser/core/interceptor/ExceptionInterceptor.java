package com.tmser.core.interceptor;


import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.tmser.core.vo.OperResult;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ExceptionMappingInterceptor;

/**
 * 自定义ajax 异步请求拦截器。扩展自ExceptionMappingInterceptor，覆盖了其intercept方法，
 * 如果ajax 请求发生异常，该拦截器将进行拦截，并返回json结果
 * {status:-99,msg:"系统异常！",data:null}
 * @author 张凯 tjx
 * @date 2014-2-13
 */
@SuppressWarnings("serial")
public class ExceptionInterceptor extends ExceptionMappingInterceptor {
	/**
	 * Exception拦截器的处理方法
	 */
	public String intercept(ActionInvocation invocation) throws Exception {
        String result = null;
		try {
			result = invocation.invoke();
		} catch (Exception e) {
			HttpServletRequest request = ServletActionContext.getRequest();
	        boolean isJson = false;
		    String contentType = request.getHeader("x-requested-with");
		    if ((contentType != null) && contentType.toLowerCase().contains("xmlhttprequest")) {
		           	isJson = true;
		    }
		    
		    if(isJson){
		    	HttpServletResponse response = ServletActionContext.getResponse();
				if (logEnabled) {
					handleLogging(e);
				}
		    	String rs = new StringBuilder("{status:")
		    		.append(OperResult.OPER_SYSTEM_ERR).append(",msg:\"系统异常！\",data:null}").toString();
		        response.setContentType("application/json;charset=utf8 ");
		        response.setHeader("Cache-Control", "no-cache");
		        response.setHeader("Expires", "0");
		        response.setHeader("Pragma", "No-cache");
		        response.setContentLength(rs.getBytes("utf-8").length);
		        PrintWriter out = response.getWriter();
		        out.print(rs);
		      return Action.NONE;
		   }else{
			   throw e;
		   }
		}
		return result;
	}
}
