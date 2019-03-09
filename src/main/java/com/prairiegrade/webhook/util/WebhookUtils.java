package com.prairiegrade.webhook.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class, pure fabrication.
 */
public final class WebhookUtils {
	private static final Logger logger = LoggerFactory.getLogger(WebhookUtils.class);
	
	private WebhookUtils() {/* util class */}

	/**
	 * Reads a text file off the classpath into a String using the Stupid Scanner Trick.
	 * @param resource location of a file on the classpath
	 * @return plaintext contents of a file off the classpath
	 */
	public static String readFromClasspath(String resource) {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
		try (java.util.Scanner s = new java.util.Scanner(in)) {
			s.useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
	}

	/**
	 * Pull the Implementation-Version value from the servlet's MANIFEST.MF, if it exists. 
	 * Errors reading the manifest will be swallowed and null will be returned.
	 * @param servletContext this app's current context
	 * @return Implementation-Version from the web app's META-INF/MANIFEST.MF, or null if no manifest was found
	 */
	public static String getVersionString(ServletContext servletContext) {
		// dump version info from war's manifest to log
		try {
			InputStream in = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF");
			if(in != null) {
				Manifest manifest = new Manifest(in);
				return manifest.getMainAttributes().getValue("Implementation-Version");
			}
		} catch (IOException e) {
			logger.warn("Failed to identify Implementation-Version", e);
		}
		return null;
	}
}
