package com.tmser.core.filter.cas;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.jasig.cas.client.authentication.DefaultGatewayResolverImpl;
import org.jasig.cas.client.authentication.GatewayResolver;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;

import com.tmser.core.constants.ConsForSystem;
import com.tmser.core.utils.CacheLoader;
import com.tmser.core.utils.StringUtils;

/**
 * 
 * 
 */
// Referenced classes of package org.jasig.cas.client.authentication:
//	            DefaultGatewayResolverImpl, GatewayResolver
public class AuthenticationFilter extends AbstractCasFilter {

	private String tempCasServerLoginUrl;
	public AuthenticationFilter() {
		renew = false;
		gateway = false;
		gatewayStorage = new DefaultGatewayResolverImpl();
		notCheckURLList = null;
	}

	protected void initInternal(FilterConfig filterConfig)
			throws ServletException {
		if (!isIgnoreInitConfiguration()) {
			super.initInternal(filterConfig);
			setCasServerLoginUrl(getPropertyFromInitParams(filterConfig,
					"casServerLoginUrl", null));
			tempCasServerLoginUrl = getPropertyFromInitParams(filterConfig,
					"casServerLoginUrl", null);
			log.trace((new StringBuilder()).append(
					"Loaded CasServerLoginUrl parameter: ").append(
					casServerLoginUrl).toString());
			setRenew(parseBoolean(getPropertyFromInitParams(filterConfig,
					"renew", "false")));
			log.trace((new StringBuilder()).append("Loaded renew parameter: ")
					.append(renew).toString());
			setGateway(parseBoolean(getPropertyFromInitParams(filterConfig,
					"gateway", "false")));
			log.trace((new StringBuilder())
					.append("Loaded gateway parameter: ").append(gateway)
					.toString());
			String gatewayStorageClass = getPropertyFromInitParams(
					filterConfig, "gatewayStorageClass", null);
			if (gatewayStorageClass != null)
				try {
					gatewayStorage = (GatewayResolver) Class.forName(
							gatewayStorageClass).newInstance();
				} catch (Exception e) {
					log.error(e, e);
					throw new ServletException(e);
				}
			String notCheckURLListStr = filterConfig
					.getInitParameter("notCheckURLList");
			notCheckURLList = notCheckURLListStr.split(",");
			String manageActionListStr = filterConfig
					.getInitParameter("manageActionList");
			manageActionList = manageActionListStr.split(",");
		}
	}

	public void init() {
		super.init();
		CommonUtils.assertNotNull(casServerLoginUrl,
				"casServerLoginUrl cannot be null.");
	}

	public final void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		String casSwitch = (String)CacheLoader.getCache(ConsForSystem.SYS_PARAM_CAS, ConsForSystem.SYS_PARAM_CAS_SWITCH, true);
		if(!ConsForSystem.ENABLE.equals(casSwitch)){//cas开关
			filterChain.doFilter(request, response);
			return;
		}
		
		HttpSession session = request.getSession(false);
		
		String path = request.getContextPath();  
        String basePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ path;
        this.setServerName(basePath);
        casServerLoginUrl = tempCasServerLoginUrl.replace("${url}", basePath);  
		String url = request.getRequestURI();
		if (StringUtils.isNotBlank(path)) {
			url = url.replace(path, "");
		}
      
		// �Ź���Ҫ���˵�URL
		boolean rs = getCheckURLResult(notCheckURLList, url);
		if (rs) {
			filterChain.doFilter(request, response);
			return;
		}

		// ��̨
		rs = false;
		rs = getCheckURLResult(manageActionList, url);
		if (rs) {
			// ��̨������cas��¼
			filterChain.doFilter(request, response);
			return;
			
		}else{
			// ǰ̨��Ҫ����cas��¼
			
			// �ж��Ƿ��Ѿ���¼��������¼��assertion��Ϊ�� ������һ��������
			Assertion assertion = session == null ? null : (Assertion) session
					.getAttribute("_const_cas_assertion_");
			if (assertion != null) {
				filterChain.doFilter(request, response);
				// response.sendRedirect(url);
				return;
			}

			// *************************û�е�¼****************************

			String serviceUrl = constructServiceUrl(request, response);
			String ticket = CommonUtils.safeGetParameter(request,
					getArtifactParameterName());

			// sessionΪ�ջ�session��û�С�_const_cas_gateway_����Ϊfalse������Ϊtrue
			boolean wasGatewayed = gatewayStorage.hasGatewayedAlready(request,
					serviceUrl);

			// ticket���ڻ�wasGateWayedΪtrue����Ź��������ticket��������֤ticket
			if (CommonUtils.isNotBlank(ticket) || wasGatewayed) {
				filterChain.doFilter(request, response);
				return;
			}
			log.debug("no ticket and no assertion found");
			String modifiedServiceUrl;
			if (gateway) {
				log.debug("setting gateway attribute in session");
				modifiedServiceUrl = gatewayStorage.storeGatewayInformation(
						request, serviceUrl);
			} else {
				modifiedServiceUrl = serviceUrl;
			}
			if (log.isDebugEnabled())
				log.debug((new StringBuilder()).append("Constructed service url: ")
						.append(modifiedServiceUrl).toString());
			String urlToRedirectTo = CommonUtils.constructRedirectUrl(
					casServerLoginUrl, getServiceParameterName(),
					modifiedServiceUrl, renew, gateway);
			if (log.isDebugEnabled())
				log.debug((new StringBuilder()).append("redirecting to \"").append(
						urlToRedirectTo).append("\"").toString());
			response.sendRedirect(urlToRedirectTo);
		}
		

		
	}

	// *****************************************************private
	// method***************************************************//

	/**
	 * �ж�URL���Ƿ��ָ������
	 * <p>
	 * ��/index.do?method=...���Ƿ��/index ���򷵻�true�����򷵻�false
	 * <p>
	 */
	private boolean getCheckURLResult(String[] checkURLList, String url) {
		// ʹ�õ����¼��URL�Ź�
		if (notCheckURLList != null) {
			boolean rs = false;
			for (String s : notCheckURLList) {
				if ("".equals(s.trim()))
					continue;
				if (url.startsWith(s.trim())) {
					rs = true;
					break;
				}
			}
			return rs;
		} else {
			return false;
		}
	}

	// ***************************************************private method
	// end***************************************************//

	public final void setRenew(boolean renew) {
		this.renew = renew;
	}

	public final void setGateway(boolean gateway) {
		this.gateway = gateway;
	}

	public final void setCasServerLoginUrl(String casServerLoginUrl) {
		this.casServerLoginUrl = casServerLoginUrl;
	}

	public final void setGatewayStorage(GatewayResolver gatewayStorage) {
		this.gatewayStorage = gatewayStorage;
	}

	public void setNotCheckURLList(String[] notCheckURLList) {
		this.notCheckURLList = notCheckURLList;
	}

	public void setManageActionList(String[] manageActionList) {
		this.manageActionList = manageActionList;
	}

	private String casServerLoginUrl;
	private boolean renew;
	private boolean gateway;
	private GatewayResolver gatewayStorage;
	private String[] notCheckURLList;
	private String[] manageActionList;
}
