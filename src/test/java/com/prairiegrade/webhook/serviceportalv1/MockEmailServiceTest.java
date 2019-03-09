package com.prairiegrade.webhook.serviceportalv1;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

import com.prairiegrade.webhook.serviceportalv1.EmailService;
import com.prairiegrade.webhook.serviceportalv1.MockEmailService;

public class MockEmailServiceTest {

	@Test
	public void testSend() throws MessagingException {
		EmailService service = new MockEmailService();
		MimeMessage message = service.createMessage();
		message.setContent("SOME TEXT", "text/plain");
		message.addRecipient(Message.RecipientType.TO, new InternetAddress("to@example.com"));
		message.addFrom(new InternetAddress[] {new InternetAddress("from@example.com")});
		service.send(message);
	}
}
