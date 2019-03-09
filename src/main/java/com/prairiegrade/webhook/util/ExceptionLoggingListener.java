package com.prairiegrade.webhook.util;

import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log all exceptions here at the error level.
 * @see GenericExceptionMapper
 */
@Provider
public class ExceptionLoggingListener implements ApplicationEventListener, RequestEventListener {
	private static final Logger logger = LoggerFactory.getLogger(ExceptionLoggingListener.class);

	@Override
	public void onEvent(RequestEvent event) {
		if (event.getType() == RequestEvent.Type.ON_EXCEPTION) {
			String uri = event.getUriInfo().getRequestUri().toString();
			logger.error("{}", uri, event.getException().getCause());
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
