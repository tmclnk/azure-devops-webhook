package com.prairiegrade.webhook.orchestrator;

/**
 * Entity for Service Portal ticket fields.
 */
public class SPTicket {
	private String title;
	private String createdByEmail;
	private String supportTeam;

	public String getSupportTeam() {
		return supportTeam;
	}

	public void setSupportTeam(String supportTeam) {
		this.supportTeam = supportTeam;
	}

	public String getCreatedByEmail() {
		return createdByEmail;
	}

	public void setCreatedByEmail(String createdByEmail) {
		this.createdByEmail = createdByEmail;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String summary) {
		this.title = summary;
	}

}
