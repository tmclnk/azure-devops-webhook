package com.prairiegrade.webhook.serviceportalv1;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mock {@link EmailService} which writes messages to its {@link #logger} rather than a real SMTP server.
 */
public class MockEmailService implements EmailService {
	private static final Logger logger = LoggerFactory.getLogger(MockEmailService.class);
	
	@Override
	public MimeMessage createMessage() {
		return new MimeMessage((Session)null);
	}

	/**
	 * At least one {@link javax.mail.Message.RecipientType#TO} and {@link javax.mail.Message.RecipientType#FROM}
	 * is required.
	 * @param message message to dump to log
	 */
	@Override
	public void send(MimeMessage message) throws MessagingException {
		StringWriter sw = new StringWriter();
		PrintWriter out = new PrintWriter(sw);
		final String to = Arrays.asList(message.getRecipients(RecipientType.TO)).stream().map(Address::toString).collect(Collectors.joining());
		out.printf("To: %s", to);
		out.println();
	
		final String from = Arrays.asList(message.getFrom()).stream().map(Address::toString).collect(Collectors.joining());
		out.printf("From: %s", from);
		out.println();
		
		out.printf("Subject: %s", message.getSubject());
		out.println();
		
		try {
			if(message.getContentType().equals("text/plain")) {
				String content = message.getContent().toString();
				out.println(content);
			} else {
				logger.info("Unknown content type: {}", message.getContent());
			}
			String content = sw.toString().replaceAll("(?m)^", "> ");
			String newline = System.lineSeparator();
			logger.info("{}{}", newline, content);
		} catch (IOException e) {
			logger.error("Failed to log message content", e);
		}
		
	}

}
