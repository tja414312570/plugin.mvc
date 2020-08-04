package com.yanan.framework.webmvc.response;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.webmvc.response.annotations.Redirect;
import com.yanan.framework.webmvc.ServletBean;

@Register(attribute="com.yanan.framework.webmvc.response.annotations.Redirect")
public class RedirectResponseHandler implements ResponseHandler{

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object handlerResult,Annotation annotation,
			ServletBean servletBean) throws ServletException, IOException {
		StringBuilder sb = new StringBuilder(handlerResult.toString());
		if(annotation!=null)
			sb.insert(0, ((Redirect)annotation).prefix()).append(((Redirect)annotation).suffix());
		response.sendRedirect(sb.toString());
	}

}