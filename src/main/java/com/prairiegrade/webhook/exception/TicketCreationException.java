package com.prairiegrade.webhook.exception;

/**
 * Thrown when Service Portal ticket creation fails.
 */
public class TicketCreationException extends WebhookException {
	private static final long serialVersionUID = -6516127269520792563L;
	public TicketCreationException(String message) {
		super(message);
	}

	public TicketCreationException(Throwable cause) {
		super(cause);
	}

	public TicketCreationException(String message, Throwable cause) {
		super(message, cause);
	}

}
