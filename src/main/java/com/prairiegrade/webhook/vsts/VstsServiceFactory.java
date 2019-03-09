package com.prairiegrade.webhook.vsts;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Creates and configures {@link AzureDevOpsFacade} instances so they can connect.
 */
@Named
public class VstsServiceFactory {
	public static final String DEFAULT_TOKENS = "azure-devops-tokens.properties";
	private static final Logger logger = LoggerFactory.getLogger(VstsServiceFactory.class);

	/** 
	 * Keys are Azure DevOps Organization names, values are Personal Access Tokens 
	 * with Work Item access to that Organization. */
	private final Properties properties;

	/**
	 * @param props keys are Organizations, Values are PATs
	 */
	public VstsServiceFactory(Properties props) {
		this.properties = props;
	}

	/**
	 * Creates factory using {@value #DEFAULT_TOKENS} file from classpath.
	 */
	public VstsServiceFactory() {
		final String propertiesFile = DEFAULT_TOKENS;
		properties = new Properties();
		InputStream in = getClass().getClassLoader().getResourceAsStream(propertiesFile);
		try {
			properties.load(in);
		} catch (IOException e) {
			throw new VstsConfigException("Failed to configure VstsServiceFactory from classpath:" + propertiesFile, e);
		}
	}
	
	public AzureDevOpsFacade getInstance(final String organization, String project) {
		String pat = properties.getProperty(organization);
		
		if(pat == null) {
			throw new IllegalArgumentException(organization + ".pat not specified");
		}

		return new JerseyVstsService(organization, project, pat);
	}

	/**
	 * Looks for 
	 * <pre>
	 * { 
	 *  "resourceContainers": {
     *    "project": {
     *      "id": "02366206-5356-414a-bcbb-7c31c7849049",
     *      "baseUrl": "https://prairiegrade.visualstudio.com/"
     *	   }
     *   }
	 * }
	 * </pre>
	 * And the first {@code System.TeamProject} value found in the json, e.g.
     * <pre>
     * {
     *   "resource":{
     *       "revision": {
	 *       	"id": 795,
	 *       	"rev": 6,
	 *       	"fields": {
     *       	  "System.TeamProject": "ocio-vsts-extensions",
	 *       	}
     *       }
     *   }
     * }
     * </pre>
	 * @param event a notification event as sent from VSTS
	 * @return a {@link AzureDevOpsFacade} configured to respond to the project that pushed out the given event
	 */
	public AzureDevOpsFacade getInstanceFromEventJson(JsonNode event) {
		String url = event.at("/resourceContainers/project/baseUrl").asText();
		
		if(url.isEmpty()) {
			logger.warn("/resourceContainers/project/baseUrl not found.  Checking /resource/url...");
			url = event.at("/resource/url").asText();
			if(url.isEmpty()) {
				logger.warn("/resource/url not found");
				throw new IllegalArgumentException("Failed to identify project url");
			}
		}

		// http://fabrikam-fiber-inc.visualstudio.com/DefaultCollection/_apis/wit/workItems/5/updates/2
		Pattern pattern = Pattern.compile("https?://([\\w-]+)\\.visualstudio.com/.*");
		Matcher matcher = pattern.matcher(url);
		final String accountName;
		if (matcher.matches()) {
			accountName = matcher.group(1);
		} else {
			throw new IllegalArgumentException("Failed to identify account name from " + url);
		}
		
		
		final String project = event.findValue("System.TeamProject").asText();
		if(project == null) {
			throw new IllegalArgumentException("Failed to find property System.TeamProject");
		}
	
		return getInstance(accountName, project);
	}
}
