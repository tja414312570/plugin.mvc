package com.yanan.framework.token;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 令牌池，令牌池化管理
 * @author yanan
 *
 */
public class TokenPool {
	//token池
	private static Map<String, Reference<Token>> tokens = new HashMap<>();
	//线程Token变量
	private static ThreadLocal<Reference<Token>> tokenLocal = new InheritableThreadLocal<>();
	
	public static Map<String, Reference<Token>> getTokenMap() {
		return tokens;
	}
	/**
	 * 获取当前线程的Token
	 * @return 获取当前线程Token
	 */
	public static Token getToken(){
		Reference<Token> reference = tokenLocal.get();
		Token token;
		if(reference == null || (token = reference.get()) == null) 
			return null;
		if(!tokens.containsKey(token.getId())) {
			tokens.remove(token.getId());
			return null;
		}
		token.refresh();
		return token;
	}
	public static Reference<Token> getTokenRefrence(String tokenId) {
		return tokens.get(tokenId);
	}
	public static Token getToken(String tokenId) {
		Reference<Token> reference = tokens.get(tokenId);
		if(reference == null)
			return null;
		Token token = reference.get();
		tokenLocal.set(reference);
		return token;
	}
	
	public static void setToken(Token token) {
		Reference<Token> reference = new SoftReference<Token>(token);
		tokenLocal.set(reference);
		tokens.put(token.getId(), reference);
	}
	public static void deleteToken() {
		Token token = getToken();
		if(token != null) {
			TokenPool.deleteToken(token.getId());
		}
	}
	/**
	 * 删除token
	 * @param tokenId
	 */
	public static void deleteToken(String tokenId) {
		Reference<Token> reference = getTokenRefrence(tokenId);
		if(reference == null)
			return;
		Token token = reference.get();
		if(token == null) {
			tokens.remove(tokenId);
			return;
		}
		reference.clear();
		token.clearToken();
		tokens.remove(tokenId);
	}

	public void destory() {
		tokens.clear();
		tokens=null;
	}
	
	public static boolean hasToken(String tokenId) {
		return tokens.containsKey(tokenId);
	}

}