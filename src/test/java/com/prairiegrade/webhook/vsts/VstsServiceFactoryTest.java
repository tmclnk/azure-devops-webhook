package com.prairiegrade.webhook.vsts;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prairiegrade.webhook.vsts.AzureDevOpsFacade;
import com.prairiegrade.webhook.vsts.VstsServiceFactory;

public class VstsServiceFactoryTest {

	private static final String ORGANIZATION = "prairiegrade";
	private static final String PROJECT = "DevOps Infrastructure";

	@Test
	public void testGetInstance() {
		VstsServiceFactory factory = new VstsServiceFactory();
		AzureDevOpsFacade service = factory.getInstance(ORGANIZATION, PROJECT);
		assertNotNull(service);
	}
	
	@Test
	public void testGetInstanceFromJson() throws IOException {
		Properties props = new Properties();
		props.load(getClass().getClassLoader().getResourceAsStream(VstsServiceFactory.DEFAULT_TOKENS));
		VstsServiceFactory factory = new VstsServiceFactory(props);
		InputStream in = getClass().getClassLoader().getResourceAsStream("workitem.updated.json");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readTree(in);
		assertNotNull(factory.getInstanceFromEventJson(json));
	}
}
