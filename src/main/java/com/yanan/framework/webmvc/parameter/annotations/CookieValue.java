package com.yanan.framework.webmvc.parameter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yanan.framework.webmvc.annotations.restful.ParameterType;
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@ParameterType
public @interface CookieValue {
	/**
	 * 参数名称
	 * @return
	 */
	String value() default "";
	/**
	 * 默认值
	 * @return
	 */
	String defaultValue() default "";
	
	}