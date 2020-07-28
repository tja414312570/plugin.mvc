package com.yanan.frame.servlets.response;

import java.lang.annotation.Annotation;

import com.yanan.frame.plugin.annotations.Service;

@Service
public interface MethodAnnotationType {
	Class<Annotation>[] getSupportAnnotationType();
}
