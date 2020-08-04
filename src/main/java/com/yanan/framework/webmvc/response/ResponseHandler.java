package com.yanan.framework.webmvc.response;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.webmvc.ServletBean;

@Service
public interface ResponseHandler {

	void render(HttpServletRequest request, HttpServletResponse response, Object handlerResult,
			Annotation responseAnnotation, ServletBean servletBean) throws ServletException, IOException;

}