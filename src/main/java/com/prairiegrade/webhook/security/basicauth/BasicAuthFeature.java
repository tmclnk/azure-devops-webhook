package com.prairiegrade.webhook.security.basicauth;

import java.io.IOException;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

/**
 * Registers a simple {@link BasicAuthenticationFilter}. You can use this as an alternative to a tomcat
 * security realm.
 * @see org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature
 * @see PropertiesUsernamePasswordAuthService#FILENAME
 */
public class BasicAuthFeature implements DynamicFeature {
	private final UsernamePasswordAuthService propertiesAuthService = new PropertiesUsernamePasswordAuthService();
	private final TokenListAuthService tokenAuthService;

	public BasicAuthFeature() throws IOException {
		tokenAuthService = new TokenListAuthService();
	}
	
	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		BasicAuthenticationFilter basicAuthenticationFilter = new BasicAuthenticationFilter(propertiesAuthService, tokenAuthService);
		context.register(basicAuthenticationFilter);
	}
}
