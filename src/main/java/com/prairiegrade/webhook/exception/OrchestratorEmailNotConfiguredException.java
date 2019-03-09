package com.prairiegrade.webhook.exception;

/**
 * Thrown when an orchestrator email address cannot be determined.
 * @see com.prairiegrade.webhook.serviceportalv1.EmailTicketServiceV1#ORCHESTRATOR_EMAIL
 */
public class OrchestratorEmailNotConfiguredException extends TicketCreationException {
	private static final long serialVersionUID = -4462901951903691231L;
	public OrchestratorEmailNotConfiguredException(String message) {
		super(message);
	}


}
