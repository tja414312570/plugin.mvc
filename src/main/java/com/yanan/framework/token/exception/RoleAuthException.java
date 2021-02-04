package com.yanan.framework.token.exception;

public class RoleAuthException extends RuntimeException {

	public RoleAuthException(String msg) {
		super(msg);
	}

	public RoleAuthException() {
		super("the requierd role is not found");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3389324287830364401L;

}