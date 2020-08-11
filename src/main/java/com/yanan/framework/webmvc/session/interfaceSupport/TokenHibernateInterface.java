package com.yanan.framework.webmvc.session.interfaceSupport;


import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.webmvc.session.entity.TokenCell;

@Service
public interface TokenHibernateInterface {
/************************token 基础部分***********************/
	boolean containerToken(String tokenId);

	void addToken(TokenCell tokenCell);

	TokenCell getToken(String tokenId);
	
	void destory(String tokenId);
/****************************数据持久化部分*******************/
/*****map**********/
	Object get(String tokenId, String key);

	void set(String tokenId, String key, Object value);
	
	void clear(String tokenId);
	
	void remove(String tokenId,String key);
/****************************角色部分*************************/

	void addRole(String tokenId, String... role);

	void clearRole(String tokenId);

	void removeRole(String tokenId, String role);

}