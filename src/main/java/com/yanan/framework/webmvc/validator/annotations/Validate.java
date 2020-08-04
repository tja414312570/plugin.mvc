package com.yanan.framework.webmvc.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解专用于pojo类型的验证，
 * 被标识的参数或类或字段都会被解包验证
 * @author yanan
 *
 */
@Target({ElementType.FIELD,ElementType.TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
	Class<?>[] groups() default { };
}