package com.yanan.framework.webmvc.response;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.webmvc.response.annotations.Forward;
import com.yanan.framework.webmvc.ServletBean;

@Register(attribute="com.yanan.framework.webmvc.response.annotations.Forward")
public class ForwardResponseHandler implements ResponseHandler{

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object handlerResult,Annotation annotation,
			ServletBean servletBean) throws ServletException, IOException {
		StringBuilder sb = new StringBuilder(handlerResult.toString());
		if(annotation!=null)
			sb.insert(0, ((Forward)annotation).prefix()).append(((Forward)annotation).suffix());
		request.setAttribute("javax.servlet.forward.request_uri", request.getRequestURI());
		request.setAttribute("javax.servlet.forward.context_path",request.getContextPath());
		request.setAttribute("javax.servlet.forward.servlet_path",request.getServletPath());
		request.setAttribute("javax.servlet.forward.path_info",request.getPathInfo());
		request.setAttribute("javax.servlet.forward.query_string",request.getQueryString());
		request.getRequestDispatcher(sb.toString()).forward(request, response);
	}

}