package com.prairiegrade.webhook.security.basicauth;

/**
 * Defines minimal interface for username/password authentication. It is reasonable to use
 * this interface for token-based authentication as well.
 */
@FunctionalInterface
public interface UsernamePasswordAuthService {
	/**
	 * @param username may be optional in some implementations
	 * @param password mandatory
	 * @return true if the username/password combination is valid
	 */
	boolean isValid(String username, String password);
}
