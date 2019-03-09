package com.prairiegrade.webhook.security;

/**
 * Unexpected error while trying to authenticate, e.g. configuration error.
 */
public class ApiAuthException extends RuntimeException {
	private static final long serialVersionUID = -3776571243614070418L;

	public ApiAuthException(String message, Throwable cause) {
		super(message, cause);
	}
}
