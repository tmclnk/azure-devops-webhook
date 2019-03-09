package com.prairiegrade.webhook.security.basicauth;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles Basic Auth requests.  If the username and password are valid, will assign a {@link BasicAuthPrincipal}
 * to the {@link SecurityContext}.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
class BasicAuthenticationFilter implements ContainerRequestFilter {
	private static final Logger logger = LoggerFactory.getLogger(BasicAuthenticationFilter.class);
	private static final String AUTHENTICATION_SCHEME = "[B|b]asic";

	@Context
	private ResourceInfo resourceInfo;
	private final List<UsernamePasswordAuthService> authService;
	
	public BasicAuthenticationFilter(UsernamePasswordAuthService ...authService) {
		this.authService = Arrays.asList(authService);
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext) {
		MultivaluedMap<String, String> headers = requestContext.getHeaders();
		final List<String> authorization = headers.get(HttpHeaders.AUTHORIZATION);

		// If no authorization information present; block access
		if (authorization == null || authorization.isEmpty()) {
			final Response missingAuthHeader = Response.status(Response.Status.UNAUTHORIZED).entity(HttpHeaders.AUTHORIZATION + " header is required").build();
			requestContext.abortWith(missingAuthHeader);
			return;
		}

		// basic-auth token handling
		final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");
		String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));
		final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
		final String username;
		final String password;
		if(usernameAndPassword.startsWith(":")) {
			username = null;
			password = tokenizer.nextToken();
		} else {
			username = tokenizer.nextToken();
			password = tokenizer.nextToken();
		}

		authService.forEach( a->{
			if(a.isValid(username, password)) {
				logger.info("Principal assigned {}", username == null ? "" : username);
				SecurityContextWrapper wrapper = new SecurityContextWrapper(requestContext.getSecurityContext());
				wrapper.setUserPrincipal(new BasicAuthPrincipal(username));
				requestContext.setSecurityContext(wrapper);
			} else {
				logger.debug("No principal for {}", username);
				// don't set principal
			}
		});
		
	}
}
