package com.prairiegrade.webhook.vsts;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Combines Azure DevOps REST methods into a single facade.
 */
public interface AzureDevOpsFacade {
	public JsonNode getWorkItem(int id);
}
