package com.yanan.frame.servlets.parameter;

import java.lang.annotation.Annotation;

import com.yanan.frame.plugin.annotations.Service;

@Service
public interface ParameterAnnotationType {
	Class<Annotation>[] getSupportAnnotationType();
}
