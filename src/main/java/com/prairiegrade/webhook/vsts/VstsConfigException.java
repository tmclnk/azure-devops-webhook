package com.prairiegrade.webhook.vsts;

public class VstsConfigException extends RuntimeException {
	private static final long serialVersionUID = -4628504684048030085L;

	public VstsConfigException(String message) {
		super(message);
	}

	public VstsConfigException(Throwable cause) {
		super(cause);
	}

	public VstsConfigException(String message, Throwable cause) {
		super(message, cause);
	}
}
