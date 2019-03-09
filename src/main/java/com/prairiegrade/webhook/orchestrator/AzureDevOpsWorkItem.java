package com.prairiegrade.webhook.orchestrator;

/**
 * Entity for bare-minumum Azure DevOps work item fields. 
 */
public class AzureDevOpsWorkItem {
	private String project;
	private String uri;
	private String htmlLink;
	private Integer id;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getHtmlLink() {
		return htmlLink;
	}
	public void setHtmlLink(String htmlLink) {
		this.htmlLink = htmlLink;
	}
	public String getUri() {
		return uri;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
}
