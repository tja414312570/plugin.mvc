package com.yanan.framework.webmvc.response;

import java.lang.annotation.Annotation;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.webmvc.response.annotations.ResponseType;

@Register
public class ResponseTypeSupportAnnotation implements MethodAnnotationType{

	@SuppressWarnings("unchecked")
	@Override
	public Class<Annotation>[] getSupportAnnotationType() {
		Class<?>[] annos = {ResponseType.class};
		return (Class<Annotation>[]) annos;
	}

}