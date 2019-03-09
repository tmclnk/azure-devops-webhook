package com.prairiegrade.webhook.security.basicauth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.prairiegrade.webhook.security.basicauth.TokenListAuthService;

public class TokenListAuthServiceTest {
	
	@Test
	public void testLoad() {
		TokenListAuthService service = new TokenListAuthService("test-api-tokens.txt");
		assertTrue(service.isValid(null, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"));
		assertTrue(service.isValid(null, "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy"));
		assertTrue(service.isValid(null, "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"));
		assertTrue(service.isValid("anystring", "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"));
		assertFalse(service.isValid("user",  "pass"));
		assertFalse(service.isValid(null,  null));
	}

	@Test
	public void testIsValid() {
		TokenListAuthService service = new TokenListAuthService("missingfile");
		assertFalse(service.isValid(null, "something"));
	}
}
