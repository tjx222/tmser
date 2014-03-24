package com.tmser.core.filter.cas;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.session.SingleSignOutHandler;
import org.jasig.cas.client.util.AbstractConfigurationFilter;
import org.jasig.cas.client.util.CommonUtils;

/**
 * 单点登出过滤器
 * @author jfyang
 *
 */
//Referenced classes of package org.jasig.cas.client.session:
//         SingleSignOutHandler, SessionMappingStorage
public final class SingleSignOutFilter extends AbstractConfigurationFilter {

	public SingleSignOutFilter() {
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		if (!isIgnoreInitConfiguration()) {
			handler.setArtifactParameterName(getPropertyFromInitParams(
					filterConfig, "artifactParameterName", "ticket"));
			handler.setLogoutParameterName(getPropertyFromInitParams(
					filterConfig, "logoutParameterName", "logoutRequest"));
		}
		handler.init();
	}

	public void setArtifactParameterName(String name) {
		handler.setArtifactParameterName(name);
	}

	public void setLogoutParameterName(String name) {
		handler.setLogoutParameterName(name);
	}

	public void setSessionMappingStorage(SessionMappingStorage storage) {
		handler.setSessionMappingStorage(storage);
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		if (handler.isTokenRequest(request)||CommonUtils.isNotBlank(request.getParameter("ticket"))) {
			handler.recordSession(request);
		} else {
			if (handler.isLogoutRequest(request)) {				
				handler.destroySession(request);
				return;
			}
			log.trace((new StringBuilder()).append("Ignoring URI ").append(
					request.getRequestURI()).toString());
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void destroy() {
	}

	protected static SingleSignOutHandler getSingleSignOutHandler() {
		return handler;
	}

	private static final SingleSignOutHandler handler = new SingleSignOutHandler();

}

/*
 * DECOMPILATION REPORT
 * 
 * Decompiled from:
 * E:\QXPT\res2area\WebRoot\WEB-INF\lib\cas-client-core-3.2.1.jar Total time:
 * 213 ms Jad reported messages/errors: Exit status: 0 Caught exceptions:
 */