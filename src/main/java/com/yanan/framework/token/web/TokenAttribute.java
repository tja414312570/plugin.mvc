package com.yanan.framework.token.web;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.yanan.framework.webmvc.annotations.restful.ParameterType;
@ParameterType
@Retention(RetentionPolicy.RUNTIME)
public @interface TokenAttribute {

	String value() default "";

}