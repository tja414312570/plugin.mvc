package com.yanan.framework.webmvc.annotations.restful;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE )
@Retention(RetentionPolicy.RUNTIME)
public @interface PathType {
}