package com.yanan.frame.servlets.session.interfaceSupport;

import com.yanan.frame.plugin.annotations.Service;
import com.yanan.frame.servlets.session.Token;

/**
 * Token监听
 * @author yanan
 *
 */
@Service
public interface TokenListener {
	
	void init(Token token);
	
	void destory(Token token);
	
}