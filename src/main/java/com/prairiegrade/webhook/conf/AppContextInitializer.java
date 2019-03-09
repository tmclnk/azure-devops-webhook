package com.prairiegrade.webhook.conf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * Load PropertySources by checking contents of {@value ENV_VARIABLE}.
 */
public class AppContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	private static final Logger logger = LoggerFactory.getLogger(WebAppInitializer.class);
	private static final String ENV_VARIABLE = "WEBHOOK_CONFIG";

	@Override
	public void initialize(ConfigurableApplicationContext context) {
		String catalinaHome = System.getenv("CATALINA_HOME");
		String catalinaBase = System.getenv("CATALINA_BASE");
		
		logger.info("CATALINA_HOME: {}", catalinaHome);
		logger.info("CATALINA_BASE: {}", catalinaBase);
		
		String webhookConfig;
		if ((webhookConfig = System.getenv(ENV_VARIABLE)) != null) {
			Path path = Paths.get(webhookConfig);
			if (path.toFile().exists()) {
				logger.info("Using {}", path);
				try (InputStream in = Files.newInputStream(path)) {
					Properties props = new Properties();
					props.load(in);
					PropertiesPropertySource propertySource = new PropertiesPropertySource("file", props);
					context.getEnvironment().getPropertySources().addFirst(propertySource);
				} catch (IOException e) {
					logger.error("Failed to load {}", webhookConfig);
				}
			}
		}
	}

}
