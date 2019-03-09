package com.prairiegrade.webhook.security.basicauth;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

class SecurityContextWrapper implements SecurityContext {
	private SecurityContext delegate;
	private Principal userPrincipal;

	public void setUserPrincipal(Principal userPrincipal) {
		this.userPrincipal = userPrincipal;
	}

	public SecurityContextWrapper(SecurityContext delegate) {
		this.delegate = delegate;
	}

	public Principal getUserPrincipal() {
		return userPrincipal != null ? userPrincipal : delegate.getUserPrincipal();
	}

	/**
	 * There's only one role; if you're authenticated, you're in it.
	 */
	public boolean isUserInRole(String role) {
		return true;
	}

	public boolean isSecure() {
		return delegate.isSecure();
	}

	public String getAuthenticationScheme() {
		return "Basic";
	}

}