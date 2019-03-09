package com.prairiegrade.webhook.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Dumps JSON for a {@link MimeMessage} so that they can be used as entities.
 */
@Provider
@Produces("application/json")
public class MimeMessageBodyWriter implements MessageBodyWriter<MimeMessage> {
	private static final Logger logger = LoggerFactory.getLogger(MimeMessageBodyWriter.class);
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return MimeMessage.class.isAssignableFrom(type);
	}

	@Override
	public void writeTo(MimeMessage t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException {
		PrintWriter out = new PrintWriter(entityStream);
	
		Map<String, String> map = new HashMap<>();
		try {
			map.put("from", Arrays.asList(t.getFrom()).stream().map(Address::toString).collect(Collectors.joining(",")));
			map.put("to", Arrays.asList(t.getAllRecipients()).stream().map(Address::toString).collect(Collectors.joining(",")));
			map.put("subject", t.getSubject());
			map.put("body", t.getContent().toString());
		} catch (MessagingException e) {
			logger.warn("Failed to dump message body", e);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(out, map);
			
		out.flush();
		out.close();
	}
}
