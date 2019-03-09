package com.prairiegrade.webhook.util;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

import com.prairiegrade.webhook.serviceportalv1.EmailService;
import com.prairiegrade.webhook.serviceportalv1.MockEmailService;
import com.prairiegrade.webhook.util.MimeMessageBodyWriter;

public class MimeMessageBodyWriterTest {

	@Test
	public void testWriteTo() throws AddressException, MessagingException, IOException {
		MimeMessageBodyWriter writer = new MimeMessageBodyWriter();
		
		
		EmailService service = new MockEmailService();
		MimeMessage message = service.createMessage();
		message.setContent("SOME TEXT", "text/plain");
		message.addRecipient(Message.RecipientType.TO, new InternetAddress("to@example.com"));
		message.addFrom(new InternetAddress[] {new InternetAddress("from@example.com")});
	
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.writeTo(message, null, null, null, null, null, out);
		
		String expected = "{\"subject\":null,\"from\":\"from@example.com\",\"to\":\"to@example.com\",\"body\":\"SOME TEXT\"}";
		assertEquals(expected, out.toString());
	}

}
