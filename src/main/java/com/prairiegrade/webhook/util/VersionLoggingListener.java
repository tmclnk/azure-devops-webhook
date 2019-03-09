package com.prairiegrade.webhook.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log the version number from the manifest.
 * @see WebhookUtils#getVersionString(ServletContext)
 */
@Provider
public class VersionLoggingListener implements ApplicationEventListener, RequestEventListener {
	private static final Logger logger = LoggerFactory.getLogger(VersionLoggingListener.class);

	@Context 
	private ServletContext servletContext;
	
	@Context
	private HttpServletRequest request;
	
	@Override
	public void onEvent(RequestEvent event) {
		if (event.getType() == RequestEvent.Type.RESOURCE_METHOD_START) {
			String path = request.getRequestURI();
			final String version = WebhookUtils.getVersionString(servletContext);
			logger.info("[{}] {}", version, path);
		}
	}

	@Override
	public void onEvent(ApplicationEvent event) {
		// do nothing
	}

	@Override
	public RequestEventListener onRequest(RequestEvent requestEvent) {
		return this;
	}

}
