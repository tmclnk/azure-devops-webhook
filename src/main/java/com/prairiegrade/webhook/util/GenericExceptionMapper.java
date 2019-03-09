package com.prairiegrade.webhook.util;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * When exceptions are thrown, return a generic 500 status and the error message only. 
 * @see ExceptionLoggingListener
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
	
	@Override
	public Response toResponse(Throwable exception) {
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(exception.getMessage() == null ? "Unknown Error" : exception.getMessage())
				.type(MediaType.TEXT_PLAIN).
				build();
	}
}
