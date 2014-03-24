package com.tmser.core.filter.cas;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jasig.cas.client.proxy.*;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.ReflectUtils;

import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;

import com.tmser.core.constants.ConsForSystem;
import com.tmser.core.utils.CacheLoader;


public class Cas20ProxyReceivingTicketValidationFilter extends
		AbstractTicketValidationFilter {

	public Cas20ProxyReceivingTicketValidationFilter() {
		proxyGrantingTicketStorage = new ProxyGrantingTicketStorageImpl();
	}

	protected void initInternal(FilterConfig filterConfig)
			throws ServletException {
		setProxyReceptorUrl(getPropertyFromInitParams(filterConfig,
				"proxyReceptorUrl", null));
		String proxyGrantingTicketStorageClass = getPropertyFromInitParams(
				filterConfig, "proxyGrantingTicketStorageClass", null);
		if (proxyGrantingTicketStorageClass != null) {
			proxyGrantingTicketStorage = (ProxyGrantingTicketStorage) ReflectUtils
					.newInstance(proxyGrantingTicketStorageClass, new Object[0]);
			if (proxyGrantingTicketStorage instanceof AbstractEncryptedProxyGrantingTicketStorageImpl) {
				AbstractEncryptedProxyGrantingTicketStorageImpl p = (AbstractEncryptedProxyGrantingTicketStorageImpl) proxyGrantingTicketStorage;
				String cipherAlgorithm = getPropertyFromInitParams(
						filterConfig, "cipherAlgorithm", "DESede");
				String secretKey = getPropertyFromInitParams(filterConfig,
						"secretKey", null);
				p.setCipherAlgorithm(cipherAlgorithm);
				try {
					if (secretKey != null)
						p.setSecretKey(secretKey);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		log.trace((new StringBuilder()).append(
				"Setting proxyReceptorUrl parameter: ")
				.append(proxyReceptorUrl).toString());
		millisBetweenCleanUps = Integer
				.parseInt(getPropertyFromInitParams(filterConfig,
						"millisBetweenCleanUps", Integer.toString(60000)));
		super.initInternal(filterConfig);
	}

	public void init() {
		super.init();
		CommonUtils.assertNotNull(proxyGrantingTicketStorage,
				"proxyGrantingTicketStorage cannot be null.");
		if (timer == null)
			timer = new Timer(true);
		if (timerTask == null)
			timerTask = new CleanUpTimerTask(proxyGrantingTicketStorage);
		timer.schedule(timerTask, millisBetweenCleanUps, millisBetweenCleanUps);
	}

	protected final TicketValidator getTicketValidator(FilterConfig filterConfig) {
		String allowAnyProxy = getPropertyFromInitParams(filterConfig,
				"acceptAnyProxy", null);
		String allowedProxyChains = getPropertyFromInitParams(filterConfig,
				"allowedProxyChains", null);
		String casServerUrlPrefix = getPropertyFromInitParams(filterConfig,
				"casServerUrlPrefix", null);
		Cas20ServiceTicketValidator validator;
		if (CommonUtils.isNotBlank(allowAnyProxy)
				|| CommonUtils.isNotBlank(allowedProxyChains)) {
			Cas20ProxyTicketValidator v = new Cas20ProxyTicketValidator(
					casServerUrlPrefix);
			v.setAcceptAnyProxy(parseBoolean(allowAnyProxy));
			v.setAllowedProxyChains(CommonUtils
					.createProxyList(allowedProxyChains));
			validator = v;
		} else {
			validator = new Cas20ServiceTicketValidator(casServerUrlPrefix);
		}
		validator.setProxyCallbackUrl(getPropertyFromInitParams(filterConfig,
				"proxyCallbackUrl", null));
		validator.setProxyGrantingTicketStorage(proxyGrantingTicketStorage);
		validator.setProxyRetriever(new Cas20ProxyRetriever(casServerUrlPrefix,
				getPropertyFromInitParams(filterConfig, "encoding", null)));
		validator.setRenew(parseBoolean(getPropertyFromInitParams(filterConfig,
				"renew", "false")));
		validator.setEncoding(getPropertyFromInitParams(filterConfig,
				"encoding", null));
		Map additionalParameters = new HashMap();
		List params = Arrays.asList(RESERVED_INIT_PARAMS);
		Enumeration e = filterConfig.getInitParameterNames();
		do {
			if (!e.hasMoreElements())
				break;
			String s = (String) e.nextElement();
			if (!params.contains(s))
				additionalParameters.put(s, filterConfig.getInitParameter(s));
		} while (true);
		validator.setCustomParameters(additionalParameters);
		validator.setHostnameVerifier(getHostnameVerifier(filterConfig));
		return validator;
	}

	public void destroy() {
		super.destroy();
		timer.cancel();
	}

	protected final boolean preFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		
	    String casSwitch = (String)CacheLoader.getCache(ConsForSystem.SYS_PARAM_CAS, ConsForSystem.SYS_PARAM_CAS_SWITCH, true);
		if(!ConsForSystem.ENABLE.equals(casSwitch)){//cas开关
			return false;
		}
		
		String path = request.getContextPath();  
        String basePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ path;
		
		this.setServerName(basePath);
		
		String requestUri = request.getRequestURI();
		if (CommonUtils.isEmpty(proxyReceptorUrl)
				|| !requestUri.endsWith(proxyReceptorUrl))
			return true;
		try {
			CommonUtils.readAndRespondToProxyReceptorRequest(request, response,
					proxyGrantingTicketStorage);
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
			throw e;
		}
		return false;
	}

	public final void setProxyReceptorUrl(String proxyReceptorUrl) {
		this.proxyReceptorUrl = proxyReceptorUrl;
	}

	public void setProxyGrantingTicketStorage(ProxyGrantingTicketStorage storage) {
		proxyGrantingTicketStorage = storage;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public void setTimerTask(TimerTask timerTask) {
		this.timerTask = timerTask;
	}

	public void setMillisBetweenCleanUps(int millisBetweenCleanUps) {
		this.millisBetweenCleanUps = millisBetweenCleanUps;
	}

	private static final String RESERVED_INIT_PARAMS[] = {
			"proxyGrantingTicketStorageClass", "proxyReceptorUrl",
			"acceptAnyProxy", "allowedProxyChains", "casServerUrlPrefix",
			"proxyCallbackUrl", "renew", "exceptionOnValidationFailure",
			"redirectAfterValidation", "useSession", "serverName", "service",
			"artifactParameterName", "serviceParameterName",
			"encodeServiceUrl", "millisBetweenCleanUps", "hostnameVerifier",
			"encoding", "config" };
	private static final int DEFAULT_MILLIS_BETWEEN_CLEANUPS = 60000;
	private String proxyReceptorUrl;
	private Timer timer;
	private TimerTask timerTask;
	private int millisBetweenCleanUps;
	private ProxyGrantingTicketStorage proxyGrantingTicketStorage;

}
