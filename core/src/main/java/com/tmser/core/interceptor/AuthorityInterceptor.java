package com.tmser.core.interceptor;

import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmser.core.utils.SpringContextHolder;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthorityInterceptor extends AbstractInterceptor {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final long serialVersionUID = 3241408339711927070L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		if(log.isDebugEnabled()) {
			log.debug("-------经过权限验证拦截器");
		}
		//用户认证验证
/*		LoginService loginService = (LoginService) SpringContextHolder.getBean(LoginServiceImpl.SERVICE_NAME);
		LoginUserInfo loginUserInfo = loginService.getCurrentUser();
		
		if (loginUserInfo == null) {
			Map<String, Object> session = invocation.getInvocationContext().getSession();
			log.debug("用户认证中。。。");
			Assertion assertion = (Assertion) session.get(AbstractCasFilter.CONST_CAS_ASSERTION);
			if (assertion != null) {
			
				AttributePrincipal principal = assertion.getPrincipal();
			
				String username = null;
				if (principal != null) {
					username = principal.getName();
				}
				
				if(!"".equals(username)) {
					UserService userService = (UserService) SpringContextHolder.getBean(UserServiceImpl.SERVICE_NAME);
					User user = userService.getUserByLoginName(username);
					String ipAddr = ServletActionContext.getRequest().getRemoteAddr();
					loginUserInfo = loginService.getLoginUserInfo(user, ipAddr);
					loginService.setCurrentUser(loginUserInfo);
				}
			}*/
			
		//}
		
		
		//用户授权验证
		//TODO ....code....
		log.debug("用户授权中。。。");
		
		return invocation.invoke();
	}

}
