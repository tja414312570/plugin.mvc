package com.yanan.framework.token;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.token.entity.TokenEntity;

/**
 * Token管理类
 * @author yanan
 *
 */
@Register
public class TokenManager{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	//token超时
	public int timeout = 3000;//默认超时
	private Thread tokenDeamon;
	private final TokenLifeDeamon tokenLifeTask;
	private Map<String, TokenEntity> tokenMap = new LinkedHashMap<String, TokenEntity>();
	public Map<String, TokenEntity> getTokenMap() {
		return tokenMap;
	}
	public TokenManager() {
		tokenLifeTask = new TokenLifeDeamon(this);
	};
	/**
	 * shutdown the token life cycle manager thread
	 */
	public void destory(){
		tokenLifeTask.shutdown();
		if(tokenDeamon.isAlive())
			tokenDeamon.interrupt();
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
		checkLifeThread();
	}
	private void checkLifeThread() {
		if((tokenDeamon == null || !tokenDeamon.isAlive()) && this.timeout >= 0) {
			tokenDeamon = new Thread(tokenLifeTask);
			tokenDeamon.setDaemon(true);
			tokenDeamon.start();
		}
	}
	public void addToken(String namespace, TokenEntity bean) {
		this.tokenMap.put(namespace, bean);
	}
	/**
	 * token实例
	 * @return instance
	 */
	public static TokenManager getInstance() {
		return PlugsFactory.getPluginsInstance(TokenManager.class);
	}
	public static void removeToken(String tokenId) {
		TokenPool.deleteToken(tokenId);
	}
	public static void addToken(Token token) {
		TokenPool.setToken(token);
	}
	public static Token getToken(String id) {
		return TokenPool.getToken(id);
	}
}