package com.yanan.framework.token.interfaces;

import com.yanan.framework.token.entity.TokenCell;

public interface TokenDao {
	
	boolean containerToken(String tokenId);

	void addToken(TokenCell tokenCell);

	TokenCell getToken(String tokenId);
	
	void destory(String tokenId);
}
