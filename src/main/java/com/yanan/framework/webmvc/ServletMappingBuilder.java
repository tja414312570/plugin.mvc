package com.yanan.framework.webmvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface ServletMappingBuilder {
	boolean builder(Class<? extends Annotation> annotationClass,Annotation annotation, Class<?> beanClass,Method beanMethod,
			ServletMapping servletMannager);

}