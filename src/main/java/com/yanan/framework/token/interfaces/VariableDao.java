package com.yanan.framework.token.interfaces;

public interface VariableDao {
	
	Object get(String tokenId, String key);

	void set(String tokenId, String key, Object value);
	
	void clear(String tokenId);
	
	void remove(String tokenId,String key);
}
