package com.yanan.framework.token.web;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.yanan.framework.token.Token;

public interface TokenFilterInterface {
	public String excute(ServletRequest request, ServletResponse response, Token token);
}