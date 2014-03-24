package com.tmser.core.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * 监控日志过滤器，用户初始化ThreadLocal中LoginUserInfo、初始化MDC
 * @author 张凯
 * @date 2014-1-25
 *
 */
public class MDCLoggerInterceptor extends AbstractInterceptor{
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	public String intercept(ActionInvocation invocation) throws Exception {
	//	try{
			long timeSt = System.currentTimeMillis();
		    HttpServletRequest httpRequest = ServletActionContext.getRequest();
			//设置请求方法到log4j
			String url = httpRequest.getRequestURI();
			url = url.substring(httpRequest.getContextPath().length());
			//初始化用户信息
/*			LoginService loginService = (LoginService) SpringContextHolder.getBean(LoginServiceImpl.SERVICE_NAME);
			LoginUserInfo loginUserInfo = loginService.getCurrentUser();*/
			//设置用户信息到log4j
/*			if(loginUserInfo != null) {
				MDC.put("userId", loginUserInfo.getUserId());
				MDC.put("userName", loginUserInfo.getUserName());
			}*/
			
			 String result = invocation.invoke();
			//超过1000毫秒警告
			long totalTime = System.currentTimeMillis() - timeSt;
			if(totalTime > 1000) {
				log.warn("服务耗时较长[{} ms]:{}",totalTime, url);
			}
			return result;
		//}finally {
			//MDC.remove("userId");
		//}
	}


}
