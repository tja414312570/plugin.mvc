package com.yanan.framework.webmvc.session.interfaceSupport;

import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.webmvc.session.Token;

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