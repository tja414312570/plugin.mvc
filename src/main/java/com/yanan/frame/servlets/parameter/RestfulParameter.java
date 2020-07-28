package com.yanan.frame.servlets.parameter;

import java.lang.annotation.Annotation;

import com.yanan.frame.plugin.annotations.Register;
import com.yanan.frame.servlets.annotations.restful.ParameterType;

@Register
public class RestfulParameter implements ParameterAnnotationType{

	@SuppressWarnings("unchecked")
	@Override
	public Class<Annotation>[] getSupportAnnotationType() {
		Class<?>[] annos ={ParameterType.class};
		return (Class<Annotation>[]) annos;
	}

}