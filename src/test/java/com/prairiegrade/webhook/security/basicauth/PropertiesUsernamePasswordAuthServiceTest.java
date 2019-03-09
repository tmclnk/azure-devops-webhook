package com.prairiegrade.webhook.security.basicauth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.prairiegrade.webhook.security.basicauth.PropertiesUsernamePasswordAuthService;
import com.prairiegrade.webhook.security.basicauth.UsernamePasswordAuthService;

public class PropertiesUsernamePasswordAuthServiceTest {
	private static final String TEST_API_USERS_PROPERTIES = "test-api-users.properties";
	private static final String USER1 = "user1";


	@Test
	public void testLoad() throws IOException {
		UsernamePasswordAuthService service = new PropertiesUsernamePasswordAuthService(TEST_API_USERS_PROPERTIES);
		assertTrue(service.isValid(USER1, "pass1"));
		assertTrue(service.isValid("user2", "pass2"));
		
		assertFalse(service.isValid(USER1,  "nope"));
		assertFalse(service.isValid(USER1,  null));
		assertFalse(service.isValid(null,  null));
	}
	

	@Test
	public void testIsValid() throws IOException {
		UsernamePasswordAuthService service = new PropertiesUsernamePasswordAuthService("missingfile");
		assertFalse(service.isValid(null, "hello"));
	}
}
