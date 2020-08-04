package com.yanan.framework.webmvc.exception;
/**
 * MVC上下文初始化异常，用于MVC初始化期间的各种异常
 * @author yanan
 *
 */
public class MVCContextInitException extends RuntimeException{
	private static final long serialVersionUID = -4263723785558342363L;
	/**
	 * MVC上下文初始化异常，用于MVC初始化期间的各种异常
	 */
	public MVCContextInitException() {
		super();
	}
	/**
	 * MVC上下文初始化异常，用于MVC初始化期间的各种异常
	 * @param msg
	 */
	public MVCContextInitException(String msg){
		super(msg);
	}
	/**
	 * MVC上下文初始化异常，用于MVC初始化期间的各种异常
	 * @param msg 异常信息
	 * @param cause 异常栈
	 */
	public MVCContextInitException(String msg,Throwable cause){
		super(msg,cause);
	}
}