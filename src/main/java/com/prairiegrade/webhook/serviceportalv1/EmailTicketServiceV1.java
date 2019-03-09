package com.prairiegrade.webhook.serviceportalv1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.JsonNode;
import com.prairiegrade.webhook.exception.OrchestratorEmailNotConfiguredException;
import com.prairiegrade.webhook.exception.TicketCreationException;
import com.prairiegrade.webhook.orchestrator.AzureDevOpsWorkItem;
import com.prairiegrade.webhook.orchestrator.OrchestratorService;
import com.prairiegrade.webhook.orchestrator.SPTicket;
import com.prairiegrade.webhook.security.Roles;
import com.prairiegrade.webhook.vsts.AzureDevOpsFacade;
import com.prairiegrade.webhook.vsts.VstsServiceFactory;

@Path("/email")
public class EmailTicketServiceV1 {
	private static final Logger logger = LoggerFactory.getLogger(EmailTicketServiceV1.class);
	
	/** 
	 * Property name of the email address which Orchestrator will monitor and create Service Portal Tickets from.
	 * This can also be used as a header in calls to {@link #onCreate(JsonNode, String, String)}.
	 */
	public static final String ORCHESTRATOR_EMAIL = "orchestrator.email";

	@Inject
	private OrchestratorService orchestratorService;
	
	@Inject 
	private Environment env;

	private boolean isTestPayload(JsonNode json) {
		return json.at("/resource/fields/System.TeamProject").asText().contains("Fabrikam");
	}

	/**
	 * Creates an email and sends it to the given orchestratorEmail, defaulting to the value from the {@value #ORCHESTRATOR_EMAIL} property.
	 * @param json
	 * @param supportTeam
	 * @param orchestratorEmail
	 * @return
	 * @throws MessagingException
	 * @throws TicketCreationException
	 */
	@POST
	@Path("/{supportTeam}/onCreate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.CREATE)
	public Response onCreate(JsonNode json, @PathParam("supportTeam") String supportTeam, @HeaderParam(ORCHESTRATOR_EMAIL) @DefaultValue("") String orchestratorEmail) throws MessagingException, TicketCreationException {
		logger.trace("JSON: {}", json);
		
		if(json == null) {
			throw new IllegalArgumentException("Missing request body");
		}

		final String title = json.at("/resource/fields/System.Title").asText();
		if(title.isEmpty()) {
			throw new TicketCreationException("System.Title not found");
		}
		
		final String teamProject = json.at("/resource/fields/System.TeamProject").asText();
		if(teamProject.isEmpty()) {
			throw new TicketCreationException("System.TeamProject not found");
		}
	
		if(orchestratorEmail == null || orchestratorEmail.isEmpty()) {
			orchestratorEmail = env.getProperty(ORCHESTRATOR_EMAIL);
			if(orchestratorEmail == null) {
				throw new OrchestratorEmailNotConfiguredException("orchestrator.email header not specified and no orchestrator.email property has been assigned");
			} else {
				logger.debug("Using configured orchestrator email {}", orchestratorEmail);
			}
		} else {
			logger.debug("Using {} header {}", ORCHESTRATOR_EMAIL, orchestratorEmail);
		}
		
		final int workItemNumber = json.findValue("resource").findValue("id").asInt();
	
		SPTicket ticket = new SPTicket();
		ticket.setTitle(title);
		ticket.setSupportTeam(supportTeam);
		
		AzureDevOpsWorkItem workItem = new AzureDevOpsWorkItem();
		workItem.setId(workItemNumber);
		workItem.setProject(teamProject);
		
		final String referenceResourceUrl = json.at("/resource/url").asText();
		workItem.setUri(referenceResourceUrl);

		final String link = json.at("/resource/_links/html/href").asText();
		workItem.setHtmlLink(link);
	
		if(isTestPayload(json)) {
			logger.info("Test payload received.");
			return Response.status(Response.Status.OK).build();
		} else {
			String createdByEmail = getCreatedByEmail(json);
			ticket.setCreatedByEmail(createdByEmail);
			MimeMessage email = orchestratorService.createTicket(ticket, workItem, orchestratorEmail);
			return Response.status(Response.Status.CREATED).entity(email).build();
		}
	}
	
	/**
	 * Determines email address for creator of original work item.  If the information is in the original
	 * request under System.CreatedBy, then we use that.  If not, we make a
	 * call to VSTS to get the full ticket, and use the CreatedBy info from <i>that</i> json.
	 * @param eventNotificationJson
	 * @return an email address, e.g. "user@example.com"
	 * @throws IllegalArgumentException if an email address couldn't be determined
	 */
	String getCreatedByEmail(JsonNode eventNotificationJson) {
		String createdBy = eventNotificationJson.findValue("System.CreatedBy").textValue();
		if(createdBy == null) {
			throw new IllegalArgumentException("System.CreatedBy is required");
		}
		if(createdBy.contains("@")) {
			logger.trace("Event json contained System.CreatedBy:'{}' ", createdBy);
			return formatEmail(createdBy);
		} else {
			logger.error("{} does not appear to be an email address", createdBy);
			throw new IllegalArgumentException("Email address is required in System.CreatedBy");
		}
	}
	
	String getCreatedByEmailFromVsts(JsonNode eventNotificationJson) {
		final int workItemNumber = eventNotificationJson.at("/resource/revision/id").asInt();
		VstsServiceFactory vstsServiceFactory = new VstsServiceFactory();
		AzureDevOpsFacade vstsService = vstsServiceFactory.getInstanceFromEventJson(eventNotificationJson);
		JsonNode workItem = vstsService.getWorkItem(workItemNumber);
		String createdBy = workItem.at("/fields/System.CreatedBy").asText();
		return formatEmail(createdBy);
	}

	/**
	 * Strip out extra stuff so this is *just* the email portion of an address.
	 * For example, {@code  McLaughlin, Tom <tom.mclaughlin@Nebraska.gov>} will
	 * be stripped down to just {@code tom.mclaughlin@Nebraska.gov}.
	 * @param fancyEmail
	 * @return an email address (only)
	 */
	static String formatEmail(String fancyEmail) {
		// McLaughlin, Tom <tom.mclaughlin@Nebraska.gov>
		Pattern pattern = Pattern.compile(".*<(.*@.*)>");
		Matcher matcher = pattern.matcher(fancyEmail);
		if(matcher.find()) {
			return matcher.group(1);
		}
		
		throw new IllegalArgumentException("Failed to pull email address from '" + fancyEmail + "'");
	}
	
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	@PermitAll
	public Response testGet(){
		logger.info("GET test");
		return Response.ok("OK!").build();
	}
	
	
	@POST
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	@RolesAllowed(Roles.CREATE)
	public Response testPost(){
		logger.info("POST test");
		return Response.ok("OK!").build();
	}
}
