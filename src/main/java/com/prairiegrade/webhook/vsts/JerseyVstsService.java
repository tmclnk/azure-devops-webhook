package com.prairiegrade.webhook.vsts;

import java.util.Base64;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * {@link AzureDevOpsFacade} that uses Personal Access Tokens and the Azure DevOps workitems REST API.
 */
class JerseyVstsService implements AzureDevOpsFacade {
	private static final Logger logger = LoggerFactory.getLogger(JerseyVstsService.class);
	
	private final String accountName;
	private final String project;
	private final String pat;

	// when using a PAT, the username doesn't matter
	private final String user = System.getProperty("user.name");
	
	/**
	 * @param accountName
	 * @param project
	 * @param user
	 * @param pat
	 */
	public JerseyVstsService(String accountName, String project, String pat) {
		super();
		this.accountName = accountName;
		this.project = project;
		this.pat = pat;
	}

	@Override
	public JsonNode getWorkItem(int id) {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		
		String url = String.format( "https://dev.azure.com/%s/%s/_apis/wit/workitems/%s?api-version=4.1", accountName, project, id);
		logger.trace("{}", url);
		
		WebTarget target = client.target(url);
	
		Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);

		// The string here is "Basic <base64 encoding of 'username:PAT'>"
		String authorization = String.format("Basic %s", Base64.getEncoder().encodeToString(String.format("%s:%s", user, pat).getBytes()));
		builder.header("Authorization", authorization);
		
		sanityCheck();
		return builder.get(JsonNode.class);
	}
	
	private void sanityCheck() {
		if(accountName.equals("fabrikam-fiber-inc")) {
			throw new IllegalArgumentException("Cannot use organization " + accountName);
		}
	}
}
