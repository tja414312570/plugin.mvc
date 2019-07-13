package com.YaNan.frame.servlets.exception;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.servlets.RestfulDispatcher;
import com.YaNan.frame.servlets.ServletBean;
import com.YaNan.frame.servlets.URLSupport;
import com.YaNan.frame.servlets.session.plugin.PermissionAuthException;
import com.YaNan.frame.servlets.validator.ParameterVerificationFailed;

/**
 * Default Servlet Exception Handler
 * 
 * @author yanan
 *
 */
@Register(priority=Integer.MAX_VALUE)
public class DefaultExceptionHandler implements ServletExceptionHandler {
	private final Logger log = LoggerFactory.getLogger( RestfulDispatcher.class);
	@Override
	public void exception(Throwable e, HttpServletRequest request, HttpServletResponse response,ServletBean bean) throws ServletException,IOException {
		response.setContentType("text/html;charset=utf-8");
		log.error(e.getMessage(),e);
		Throwable cause = e;
		while((e = e.getCause())!=null) {
			cause = e;
		}
		PrintWriter writer = response.getWriter();
		if(cause.getClass().equals(PermissionAuthException.class)) {
			response.setStatus(401);
			writer.write("an error has occurred because not permission to execute the request resource "+URLSupport.getRelativePath(request));
			writer.flush();
			writer.close();
		}else if(cause.getClass().equals(ParameterVerificationFailed.class)){
			response.setStatus(403);
			writer.write("an error has occurred because parameter verifiy faild when execute the request resource "+URLSupport.getRelativePath(request));
			writer.flush();
			writer.close();
		}else {
			throw new ServletException(e);
		}
	}
	
}