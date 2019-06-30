package com.YaNan.frame.servlets.exception;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.servlets.ServletBean;

public interface ServletExceptionHandler {

	void exception(Throwable e, HttpServletRequest request, HttpServletResponse response, ServletBean servletBean) throws ServletException,IOException;
	
}
