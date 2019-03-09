package com.prairiegrade.webhook.serviceportalv1;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prairiegrade.webhook.orchestrator.OrchestratorService;
import com.prairiegrade.webhook.serviceportalv1.EmailTicketServiceV1;

@RunWith(EasyMockRunner.class)
public class EmailTicketServiceV1IT {
	
	@TestSubject
	private EmailTicketServiceV1 service = new EmailTicketServiceV1();
	
	@Mock
	private OrchestratorService orchestratorService;

	@Test
	public void testGetCreatedByEmail() throws JsonProcessingException, IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("EmailTicketServiceV1IT/event.stub.json");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readTree(in);
		assertEquals("tom.mclaughlin@Nebraska.gov", service.getCreatedByEmailFromVsts(json));
	}

}
