package com.prairiegrade.webhook.serviceportalv1;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.Response;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prairiegrade.webhook.exception.TicketCreationException;
import com.prairiegrade.webhook.orchestrator.AzureDevOpsWorkItem;
import com.prairiegrade.webhook.orchestrator.OrchestratorService;
import com.prairiegrade.webhook.orchestrator.SPTicket;
import com.prairiegrade.webhook.serviceportalv1.EmailTicketServiceV1;

@RunWith(EasyMockRunner.class)
public class EmailTicketServiceV1Test {
	@TestSubject
	private EmailTicketServiceV1 service = new EmailTicketServiceV1();
	
	@Mock
	private OrchestratorService orchestratorService;

	@Test
	public void testGetCreatedByEmail() throws JsonProcessingException, IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("795.workitem.updated.json");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readTree(in);
		assertEquals("tom.mclaughlin@Nebraska.gov", service.getCreatedByEmail(json));
	}
	
	@Test
	public void testOnCreate() throws TicketCreationException, MessagingException, IOException {
		// set up mock internals
		orchestratorService.createTicket(anyObject(SPTicket.class), anyObject(AzureDevOpsWorkItem.class), anyObject(String.class));
		expectLastCall().andAnswer(() ->{
			SPTicket ticket = (SPTicket)getCurrentArguments()[0];
			AzureDevOpsWorkItem workItem = (AzureDevOpsWorkItem)getCurrentArguments()[1];
			assertEquals("tom.mclaughlin@Nebraska.gov", ticket.getCreatedByEmail());
			assertEquals(877, workItem.getId().intValue());
			assertEquals("test", ticket.getTitle());
	
			// make sure the *orchestrator* gets the email address passed on
			String orchestratorEmail = (String)getCurrentArguments()[2];
			assertEquals("fakeorchestrator@example.com", orchestratorEmail);
		
			MimeMessage message = new MimeMessage((Session)null);
			message.addRecipients(javax.mail.Message.RecipientType.TO, "fakeorchestrator@example.com");
			return message;
		});
		replay(orchestratorService);
	
		
		InputStream in = getClass().getClassLoader().getResourceAsStream("877.workitem.created.v1.json");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readTree(in);
		Response response = service.onCreate(json, "Mock Team", "fakeorchestrator@example.com");
		verify(orchestratorService);
		
		// verify that the response is a mime message with the assigned email address (assume it can be converted to json elsewhere)
		MimeMessage actual = (MimeMessage)response.getEntity();
		Address to = actual.getAllRecipients()[0];
		assertEquals("fakeorchestrator@example.com", to.toString());
	}
	
	@Test
	public void testOnCreateWithTestPayload() throws TicketCreationException, MessagingException, IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("test.workitem.created.v1.json");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readTree(in);
		service.onCreate(json, "Mock Team", "fakeorchestrator@example.com");
	}
	
	@Test
	public void testFormatEmail() {
		assertEquals("tom.mclaughlin@prairiegrade.com", EmailTicketServiceV1.formatEmail("McLaughlin, Tom <tom.mclaughlin@prairiegrade.com>"));
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testFormatEmailException() {
		EmailTicketServiceV1.formatEmail("McLaughlin, Tom tom.mclaughlin@prairiegrade.com");
	}

}
