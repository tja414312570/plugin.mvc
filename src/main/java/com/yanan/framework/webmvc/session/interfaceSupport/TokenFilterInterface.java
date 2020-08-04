package com.yanan.framework.webmvc.session.interfaceSupport;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.yanan.framework.webmvc.session.Token;

public interface TokenFilterInterface {
	public String excute(ServletRequest request, ServletResponse response, Token token);
}