package com.yanan.framework.token.interfaces;

import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.token.Token;

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