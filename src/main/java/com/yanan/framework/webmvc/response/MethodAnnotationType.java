package com.yanan.framework.webmvc.response;

import java.lang.annotation.Annotation;

import com.yanan.framework.plugin.annotations.Service;

@Service
public interface MethodAnnotationType {
	Class<Annotation>[] getSupportAnnotationType();
}