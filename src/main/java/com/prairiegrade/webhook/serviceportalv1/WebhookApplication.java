package com.prairiegrade.webhook.serviceportalv1;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.prairiegrade.webhook.security.basicauth.BasicAuthFeature;
import com.prairiegrade.webhook.util.ExceptionLoggingListener;
import com.prairiegrade.webhook.util.GenericExceptionMapper;
import com.prairiegrade.webhook.util.MimeMessageBodyWriter;
import com.prairiegrade.webhook.util.VersionLoggingListener;

/**
 * {@link ResourceConfig} for service portal webhook resource.
 */
@ApplicationPath("/serviceportalv1")
public class WebhookApplication extends ResourceConfig {
	public WebhookApplication() {
		Logger logger = Logger.getLogger("gov.ne.ocio.webhook");
		register(new LoggingFeature(logger, Level.INFO, LoggingFeature.Verbosity.PAYLOAD_ANY, 1000000));
		register(EmailTicketServiceV1.class);
		
		register(BasicAuthFeature.class);
		
		register(RolesAllowedDynamicFeature.class);
		register(ExceptionLoggingListener.class);
		register(VersionLoggingListener.class);
		register(GenericExceptionMapper.class);
		register(MimeMessageBodyWriter.class);
	}
}
