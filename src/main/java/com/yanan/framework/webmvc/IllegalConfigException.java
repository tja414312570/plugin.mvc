package com.yanan.framework.webmvc;

/**
 * 不合法配置异常，抛出此异常的原因是配置存在，但配置的内容的格式错误
 * @author yanan
 *
 */
public class IllegalConfigException extends RuntimeException {

	public IllegalConfigException(String msg) {
		super(msg);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -4347923203057252487L;

}