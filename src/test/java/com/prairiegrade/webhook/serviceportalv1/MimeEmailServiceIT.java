package com.prairiegrade.webhook.serviceportalv1;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import com.prairiegrade.webhook.serviceportalv1.MimeEmailService;

public class MimeEmailServiceIT {

	// if you need to test the mail service "for real", change this to an annotation,
	// but we don't want to spam anyone and we need the unit test to run
	// from anywhere
	// org.junit.Test
	public void testCreateMessage() throws IOException, MessagingException {
		// use the mail server specified in properties file
		Properties props = new Properties();
		InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties");
		props.load(in);
		String host = props.getProperty("mail.smtp.host");
		
		MimeEmailService service = new MimeEmailService(host);
		MimeMessage message = service.createMessage();
	
		// email to the current user, from the current user
		String username = System.getProperty("user.name");
		String targetEmail = String.format("%s@prairiegrade.com", username);
		
		message.setFrom(new InternetAddress(targetEmail, "JUnit Test"));
		message.setSubject(String.format("%s Test Email", getClass().getSimpleName()));
		message.addRecipient(RecipientType.TO, new InternetAddress(targetEmail));
		message.setText("Test Text " + LocalDateTime.now());
		
		service.send(message);
	}

}
