package com.prairiegrade.webhook.orchestrator;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prairiegrade.webhook.exception.TicketCreationException;
import com.prairiegrade.webhook.orchestrator.AzureDevOpsWorkItem;
import com.prairiegrade.webhook.orchestrator.OrchestratorService;
import com.prairiegrade.webhook.orchestrator.SPTicket;
import com.prairiegrade.webhook.serviceportalv1.EmailService;
import com.prairiegrade.webhook.util.WebhookUtils;

@RunWith(EasyMockRunner.class)
public class OrchestratorServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(OrchestratorServiceTest.class);

	@TestSubject
	private OrchestratorService orchestratorService = new OrchestratorService();

	@Mock
	private EmailService emailService;

	@Test
	public void testCreateEmail() throws TicketCreationException {
		SPTicket ticket = new SPTicket();
		ticket.setCreatedByEmail("createdby@example.com");
		ticket.setSupportTeam("Mock Team");
		
		AzureDevOpsWorkItem workItem = new AzureDevOpsWorkItem();
		workItem.setProject("my-project");
		workItem.setId(1234);
		workItem.setHtmlLink("https://prairiegrade.visualstudio.com/web/wi.aspx?pcguid=33828312-fa25-481a-aeec-20337b774cd0&id=795");
		
		ticket.setTitle("Sweet \"New\" Ticket & Stuff");
		String emailBody = orchestratorService.createEmailBody(ticket, workItem);
		logger.info("{}", emailBody);
		String expected = WebhookUtils.readFromClasspath("OrchestratorService/template-expected.txt");
		assertEquals(expected, emailBody);
	}


	@Test
	public void testCreateTicket() throws MessagingException, TicketCreationException {
		expect(emailService.createMessage()).andReturn(new MimeMessage((Session) null)).once();
		emailService.send(anyObject());
		expectLastCall().andAnswer(() -> {
			MimeMessage mimeMessage = (MimeMessage) getCurrentArguments()[0];
			Address to = mimeMessage.getRecipients(RecipientType.TO)[0];
			assertEquals("test@example.com", to.toString());
			assertEquals("jTrac - Mock Team - #1234 Hello World", mimeMessage.getSubject());
			assertEquals("tom.mclaughlin@Nebraska.gov", mimeMessage.getFrom()[0].toString());
			return null;
		}).once();

		AzureDevOpsWorkItem workItem = new AzureDevOpsWorkItem();
		workItem.setId(1234);
		workItem.setProject("my-test-project");
		workItem.setHtmlLink("http://somelink.com");
		
		SPTicket ticket = new SPTicket();
		ticket.setCreatedByEmail("tom.mclaughlin@Nebraska.gov");
		ticket.setTitle("Hello World");
		ticket.setSupportTeam("Mock Team");

		replay(emailService);

		orchestratorService.createTicket(ticket, workItem, "test@example.com");
		verify(emailService);
	}
}
