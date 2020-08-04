package com.yanan.framework.webmvc.parameter;

import java.lang.annotation.Annotation;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.webmvc.annotations.restful.ParameterType;

@Register
public class RestfulParameter implements ParameterAnnotationType{

	@SuppressWarnings("unchecked")
	@Override
	public Class<Annotation>[] getSupportAnnotationType() {
		Class<?>[] annos ={ParameterType.class};
		return (Class<Annotation>[]) annos;
	}

}