package com.yanan.framework.webmvc.response;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yanan.framework.webmvc.ServletBean;

public interface ServletExceptionHandler {

	void exception(Throwable e, HttpServletRequest request, HttpServletResponse response, ServletBean servletBean) throws ServletException,IOException;
	
}