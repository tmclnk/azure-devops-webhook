package com.prairiegrade.webhook.serviceportalv1;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public interface EmailService {
	MimeMessage createMessage();
	void send(MimeMessage message) throws MessagingException;
}
