package com.yanan.framework.webmvc.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否启用快速验证
 * 默认验证会验证所有带有验证注解的参数时抛出异常
 * 快速验证当一个验证不通过即抛出异常
 * @author yanan
 *
 */
@Target({ElementType.TYPE,ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FastValidate {
}