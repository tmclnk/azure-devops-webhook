package com.prairiegrade.webhook.conf;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Registers the {@link AppConfig} with the ServletContext. This will be picked automatically by Spring.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebAppInitializer implements WebApplicationInitializer {
	public static final Logger logger = LoggerFactory.getLogger(WebAppInitializer.class);

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {	
		// using spring jersey causes context loading issues in tomcat, so we need this line
		// https://stackoverflow.com/questions/33633098/java-lang-illegalstateexception-cannot-initialize-context-because-there-is-alre
		servletContext.setInitParameter("contextConfigLocation", "<NONE>");
		servletContext.setInitParameter("contextInitializerClasses", AppContextInitializer.class.getName());
	
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(AppConfig.class);
		servletContext.addListener(new ContextLoaderListener(context));
	}

}