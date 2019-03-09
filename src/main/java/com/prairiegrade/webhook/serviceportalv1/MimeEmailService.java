package com.prairiegrade.webhook.serviceportalv1;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 * Sends Mime emails using the javax.mail api. Can be used with a JNDI {@link Session}
 * or can assemble one using mail.smtp.host.
 */
public class MimeEmailService implements EmailService {
	/* Sessions are supposedly threadsafe, so we'll just share one
	 * https://stackoverflow.com/questions/12732584/threadsafety-in-javamail */
	private final Session session;

	public MimeEmailService(Session session) {
		this.session = session;
	}
	
	public MimeEmailService(String mailHost) {
		Properties props = new Properties();
		props.put("mail.smtp.host", mailHost);
		session = Session.getDefaultInstance(props);
	}
	
	@Override
	public MimeMessage createMessage() {
		return new MimeMessage(session);
	}

	@Override
	public void send(MimeMessage message) throws MessagingException {
		Transport.send(message);
	}

}
