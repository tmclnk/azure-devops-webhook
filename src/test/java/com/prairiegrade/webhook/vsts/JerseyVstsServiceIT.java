package com.prairiegrade.webhook.vsts;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.prairiegrade.webhook.conf.AppConfig;
import com.prairiegrade.webhook.vsts.AzureDevOpsFacade;
import com.prairiegrade.webhook.vsts.VstsServiceFactory;

/**
 * Makes a "real" web service call against Azure DevOps to query a work item.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={AppConfig.class})
public class JerseyVstsServiceIT {
	private Logger logger = LoggerFactory.getLogger(JerseyVstsServiceIT.class);

	private static final String TEAM_PROJECT = "DevOps Infrastructure";
	private static final int WORK_ITEM_ID = 2175;
	private static final String ORG = "prairiegrade";
	
	private VstsServiceFactory factory = new VstsServiceFactory();

	@Test
	public void testGetWorkItem() {
		AzureDevOpsFacade service = factory.getInstance(ORG, TEAM_PROJECT);
		JsonNode obj = service.getWorkItem(WORK_ITEM_ID);
		logger.trace("{}", obj.toString());
		assertEquals(WORK_ITEM_ID, obj.findValue("id").asInt());
		assertEquals(TEAM_PROJECT, obj.at("/fields/System.TeamProject").asText());
	}
}
