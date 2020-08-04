package com.yanan.framework.webmvc.validator;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.validation.ConstraintViolation;

import com.yanan.framework.plugin.handler.MethodHandler;
import com.yanan.framework.webmvc.ServletBean;

/**
 * 参数验证失败异常
 * @author yanan
 *
 */
public class ParameterVerificationFailed extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1274380312156645983L;
	/**
	 * 验证错误的结果，
	 * key可能是参数parameter或字段Field两种类型，value为验证失败的结果信息
	 */
	private Map<Object, ConstraintViolation<?>> validResult;
	

	public ParameterVerificationFailed(ServletBean servletBean, MethodHandler methodHandler, Annotation anno) {
		super("an error has occurred when verifiy parameter");
	}

	public ParameterVerificationFailed(Map<Object, ConstraintViolation<?>> validResult) {
		super("an error has occurred when verifiy parameter");
		this.setValidResult(validResult);
	}

	public Map<Object, ConstraintViolation<?>> getValidResult() {
		return validResult;
	}

	private void setValidResult(Map<Object, ConstraintViolation<?>> validResult) {
		this.validResult = validResult;
	}

}