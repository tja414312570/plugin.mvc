package com.yanan.framework.webmvc.parameter;

import java.lang.annotation.Annotation;

import com.yanan.framework.plugin.annotations.Service;

@Service
public interface ParameterAnnotationType {
	Class<Annotation>[] getSupportAnnotationType();
}