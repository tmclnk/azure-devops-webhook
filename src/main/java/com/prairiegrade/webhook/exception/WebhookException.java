package com.prairiegrade.webhook.exception;

/**
 * Top-level exception.
 */
public class WebhookException extends Exception {
	private static final long serialVersionUID = 2495241116999605446L;

	public WebhookException(String message) {
		super(message);
	}

	public WebhookException(Throwable cause) {
		super(cause);
	}

	public WebhookException(String message, Throwable cause) {
		super(message, cause);
	}

}
