package com.prairiegrade.webhook.conf;

import javax.inject.Inject;
import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.prairiegrade.webhook.orchestrator.OrchestratorService;
import com.prairiegrade.webhook.serviceportalv1.EmailService;
import com.prairiegrade.webhook.serviceportalv1.MimeEmailService;
import com.prairiegrade.webhook.serviceportalv1.MockEmailService;

/**
 * Dynamic Spring Bean config.
 */
@Configuration
@ComponentScan
@PropertySource(value="classpath:application.properties", ignoreResourceNotFound=true)
public class AppConfig {
	private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

	/**
	 * Loads an {@link EmailService} using these rules:
	 * <ol>
	 * <li>JNDI: if there's an entry in mail/Session, it is used</li>
	 * <li>Otherwise, if mail.smtp.host is specified anywhere in the {@link Environment}, a Mail Session is created and used</li>
	 * <li>Otherwise, a {@link MockEmailService} is used</li>
	 * </ol>
	 * 
	 * @param env the {@link Environment}
	 * @return {@link EmailService} built depending on the environment
	 */
	@Bean
	@Inject
	public EmailService emailService(Environment env) {
		// try to use JNDI
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			Session session = (Session) envCtx.lookup("mail/Session");
			logger.info("Using JNDI mail Session");
			return new MimeEmailService(session);
		} catch (NoClassDefFoundError e) {
			// will happen if tomcat is missing activation or mail jars
			// not a problem since there may be a mail host specified
			logger.trace("Failed to load Mail Session from JNDI", e);
		} catch(NamingException e) {
			logger.debug("Mail Session not found in JNDI");
			// no problem
		}
	
		// no JNDI?  use mail.smtp.host and build our own mail Session
		final String mailHost = env.getProperty("mail.smtp.host");
		if(mailHost == null){
			logger.warn("mail.smtp.host not specified");
			logger.warn("Email will be written to the logs");
			return new MockEmailService();
		} else {
			logger.info("mail.smtp.host={}", mailHost);
			return new MimeEmailService(mailHost);
		}
	}
	
	/**
	 * Configures {@link OrchestratorService} using orchestrator.email property.  If unset, 
	 * uses the current java user at prairiegrade.com.
	 * @return the default {@link OrchestratorService}
	 */
	@Bean
	public OrchestratorService orchestratorService() {
		return new OrchestratorService();
	}
	
}
