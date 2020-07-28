package com.yanan.frame.servlets.session.interfaceSupport;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.yanan.frame.servlets.session.Token;

public interface TokenFilterInterface {
	public String excute(ServletRequest request, ServletResponse response, Token token);
}