package com.yanan.framework.webmvc.parameter;

import java.lang.reflect.Parameter;

import com.yanan.framework.webmvc.exception.ServletRuntimeException;

public class ParameterPreparedExecution extends ServletRuntimeException {

	private Parameter parameter;

	public ParameterPreparedExecution(String msg, Exception e) {
		super(msg,e);
	}

	public ParameterPreparedExecution(String msg, Parameter paras, Exception e) {
		super(msg,e);
		this.setParameter(paras);
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8753455751685081206L;

}