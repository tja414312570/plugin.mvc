package com.yanan.frame.servlets.response;

import java.lang.annotation.Annotation;

import com.yanan.frame.plugin.annotations.Register;
import com.yanan.frame.servlets.response.annotations.ResponseType;

@Register
public class ResponseTypeSupportAnnotation implements MethodAnnotationType{

	@SuppressWarnings("unchecked")
	@Override
	public Class<Annotation>[] getSupportAnnotationType() {
		Class<?>[] annos = {ResponseType.class};
		return (Class<Annotation>[]) annos;
	}

}