package com.yanan.framework.webmvc.response;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.webmvc.response.annotations.ResponseJson;
import com.yanan.framework.webmvc.ServletBean;
import com.google.gson.Gson;

@Register(attribute = "com.yanan.framework.webmvc.response.annotations.ResponseJson")
public class JsonResponseHandler implements ResponseHandler {
	private final static Gson gson = new Gson();

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object handlerResult,
			Annotation annotation, ServletBean servletBean) throws ServletException, IOException {
		response.setContentType("application/json;charset=utf-8");
		if (!response.isCommitted()) {
			ResponseJson anno = (ResponseJson) annotation;
			response.getWriter().write(new StringBuffer(anno.prefix()).append(gson.toJson(handlerResult))
					.append(anno.suffix()).toString());
		}
	}

}