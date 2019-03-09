package com.prairiegrade.webhook.security.basicauth;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.*;

import java.util.Arrays;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import com.prairiegrade.webhook.security.basicauth.BasicAuthenticationFilter;
import com.prairiegrade.webhook.security.basicauth.UsernamePasswordAuthService;

public class BasicAuthenticationFilterTest {

	/** Test with no headers at all (should abort) */
	@Test
	public void testFilterReject() {
		BasicAuthenticationFilter filter = new BasicAuthenticationFilter();
		
		ContainerRequestContext requestContext= niceMock(ContainerRequestContext.class);
		expect(requestContext.getHeaders()).andAnswer(() ->{
			return new MultivaluedHashMap<>();
		}).once();
	
		requestContext.abortWith(anyObject());
		expectLastCall().once();
		replay(requestContext);
		
		filter.filter(requestContext);
	}

	/** Test with 2 auth services added */
	@Test
	public void testFilter() {
		UsernamePasswordAuthService authService1 = mock(UsernamePasswordAuthService.class);
		UsernamePasswordAuthService authService2 = mock(UsernamePasswordAuthService.class);
		BasicAuthenticationFilter filter = new BasicAuthenticationFilter(authService1,authService2);
	
		ContainerRequestContext requestContext= niceMock(ContainerRequestContext.class);
		
		// basic auth headers
		expect(requestContext.getHeaders()).andAnswer(() ->{
			MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
			headers.put(HttpHeaders.AUTHORIZATION, Arrays.asList("Basic dXNlcm5hbWU6cGFzc3dvcmQ="));
			return headers;
		}).once();
	
		// we need to verify that a security context was added (this is basically what we're asserting)
		requestContext.setSecurityContext(anyObject());
		expectLastCall().once();
		
		// first auth service accepts
		expect(authService1.isValid(eq("username"), eq("password"))).andReturn(false);
		
		// second auth service rejects
		expect(authService2.isValid(eq("username"), eq("password"))).andReturn(true);
	
		replay(authService1, authService2, requestContext);
		
		filter.filter(requestContext);
		verify(requestContext, authService1, authService2);
	}

}
