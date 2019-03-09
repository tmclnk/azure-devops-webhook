package com.prairiegrade.webhook.orchestrator;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prairiegrade.webhook.exception.TicketCreationException;
import com.prairiegrade.webhook.serviceportalv1.EmailService;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.UndefinedOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * Creates Service Portal tickets using emails to the Orchestrator service.
 */
public class OrchestratorService {
	private static final Logger logger = LoggerFactory.getLogger(OrchestratorService.class);
	
	/** classpath location of templates; note that the leading "/" is required */
	private static final String TEMPLATE_DIR = "/emailtemplates";
	private static final String EMAIL_TEMPLATE = "new-spticket.txt";
	
	@Inject
	private EmailService emailService;

	private final Configuration freemarkerConfig;

	/**
	 * Sets up the Freemarker template used to assemble emails.
	 */
	public OrchestratorService() {
		super();
	
		this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_28);
		TemplateLoader templateLoader = new ClassTemplateLoader(OrchestratorService.class, TEMPLATE_DIR);
		logger.info("Reading freemarker templates from {}", TEMPLATE_DIR);
		this.freemarkerConfig.setTemplateLoader(templateLoader);
		this.freemarkerConfig.setDefaultEncoding("UTF-8");
		this.freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		this.freemarkerConfig.setLogTemplateExceptions(false);
		this.freemarkerConfig.setWrapUncheckedExceptions(true);
		this.freemarkerConfig.setOutputFormat(UndefinedOutputFormat.INSTANCE);
		this.freemarkerConfig.setNumberFormat("#"); // otherwise it will put commas into numbers
	}

	/**
	 * Creates body of email using a Freemarker template.
	 * @param ticket
	 * @return email body
	 */
	String createEmailBody(SPTicket ticket, AzureDevOpsWorkItem workItem) throws TicketCreationException {
		Map<String, Object> map = new HashMap<>();
		map.put("ticket", ticket);
		map.put("workItem", workItem);
		
		try {
			Template template = this.freemarkerConfig.getTemplate(EMAIL_TEMPLATE);
			Writer out = new StringWriter();
			template.process(map, out);
			return out.toString();
		} catch (IOException | TemplateException e) {
			throw new TicketCreationException("Failed to create ticket", e);
		}
	}

	/**
	 * Sends an email to the Orchestrator, instructing it to create a new ticket.
	 * @param ticket the Service Portal ticket info
	 * @param workItem the Azure DevOps work item info
	 * @param orchestratorEmail where to send emails
	 * @return a copy of the email that was sent
	 * @throws MessagingException if email cannot be sent
	 * @throws TicketCreationException if ticket request can't be made
	 */
	public MimeMessage createTicket(SPTicket ticket, AzureDevOpsWorkItem workItem, String orchestratorEmail) throws TicketCreationException, MessagingException {
		Objects.requireNonNull(orchestratorEmail, "orchestratorEmail is required");
		if(orchestratorEmail.isEmpty()) {
			throw new TicketCreationException("empty orchestratorEmail");
		}
		Objects.requireNonNull(ticket.getSupportTeam(), "ticket.supportTeam is required");
		Objects.requireNonNull(ticket.getTitle(), "ticket.title required");
		Objects.requireNonNull(workItem.getId(), "workItem.id required");
		Objects.requireNonNull(workItem.getProject(), "workItem.project required");
		
		String subject = String.format("jTrac - %s - #%s %s", ticket.getSupportTeam(), workItem.getId(), ticket.getTitle());
		
		MimeMessage message = emailService.createMessage();
		message.setSubject(subject);
		message.addRecipients(RecipientType.TO, orchestratorEmail);
		
		// FROM address needs to be the one that requested the jtrac
		InternetAddress from = new InternetAddress(ticket.getCreatedByEmail());
		message.setFrom(from);

		String emailBody = createEmailBody(ticket, workItem);
		message.setText(emailBody);
		
		emailService.send(message);
		logger.info("[{} #{}] Ticket Requested For {}", workItem.getProject(), workItem.getId(), ticket.getSupportTeam());
		return message;
	}
}
