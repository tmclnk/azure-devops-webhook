package com.prairiegrade.webhook.security.basicauth;

import java.security.Principal;

public class BasicAuthPrincipal implements Principal {
	private String name;
	
	public BasicAuthPrincipal(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
