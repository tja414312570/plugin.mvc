package com.yanan.framework.webmvc.response;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.webmvc.validator.ParameterVerificationFailed;
import com.yanan.framework.webmvc.RestfulDispatcher;
import com.yanan.framework.webmvc.ServletBean;
import com.yanan.framework.webmvc.URLSupport;
import com.yanan.framework.token.exception.*;

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
		try {
			PrintWriter writer = response.getWriter();
			if(cause.getClass().equals(PermissionAuthException.class)
					|| cause.getClass().equals(RoleAuthException.class)) {
				response.setStatus(401);
				writer.write("<h2 style='background: #d69e9e;padding: 14px;'>an error has occurred because not permission to execute the request resource "+URLSupport.getRelativePath(request)+"</h2>");
				writer.write("<p style='text-indent: 14px;'>cause by :"+cause.getMessage());
				writer.flush();
				writer.close();
			}else if(cause.getClass().equals(ParameterVerificationFailed.class)){
				response.setStatus(403);
				writer.write("<h4 >an error has occurred because parameter verifiy faild when execute the request resource "+URLSupport.getRelativePath(request));
				writer.flush();
				writer.close();
			}else {
				throw new ServletException(e);
			}
		} catch (IllegalStateException e1) {
		}
	}
	
}