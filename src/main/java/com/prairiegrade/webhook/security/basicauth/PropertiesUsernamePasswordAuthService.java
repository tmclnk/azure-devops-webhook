package com.prairiegrade.webhook.security.basicauth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authenticates using usernames and passwords from a properties file.
 */
public class PropertiesUsernamePasswordAuthService implements UsernamePasswordAuthService {
	private static final Logger logger = LoggerFactory.getLogger(PropertiesUsernamePasswordAuthService.class);
	public static final String FILENAME = "api-users.properties";
	private final Properties props;
	
	public PropertiesUsernamePasswordAuthService(String filename) throws IOException {
		props = new Properties();
		InputStream in = getClass().getClassLoader().getResourceAsStream(filename);
		if(in != null) {
			props.load(in);
		} else {
			logger.info("{} not found", filename);
		}
	}

	public PropertiesUsernamePasswordAuthService() throws IOException {
		this(FILENAME);
	}
	
	@Override
	public boolean isValid(String username, String password) {
		if(username == null) {
			return false;
		}
		String expectedPassword = props.getProperty(username);
		
		if(expectedPassword == null) {
			logger.trace("User '{}' not found", username);
			return false;
		}
		
		return expectedPassword.equals(password);
	}
}
