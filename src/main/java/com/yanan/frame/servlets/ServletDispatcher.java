package com.yanan.frame.servlets;

import java.lang.annotation.Annotation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public interface ServletDispatcher {
	 <T extends Annotation> Class<T>[] getDispatcherAnnotation();

	ServletMappingBuilder getBuilder();

	ServletBean getServletBean(HttpServletRequest request) throws ServletException;
}