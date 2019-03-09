package com.prairiegrade.webhook.conf;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prairiegrade.webhook.util.WebhookUtils;

/**
 * Dumps startup/shutdown messages to the logs indicating the version number being started stopped.
 * This can be used to help debug parallel deployment issues.
 * @see WebhookUtils#getVersionString(ServletContext)
 */
@WebListener
public class VersionLoggingListener implements ServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(VersionLoggingListener.class);
	private String version;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		version = WebhookUtils.getVersionString(context);
		if (version != null) {
			logger.info("Starting {}", version);
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (version != null) {
			logger.info("Stopping {}", version);
		}
	}
}
