package com.yanan.framework.token.interfaces;

public interface RoleDao {
	
	void setRole(String tokenId, String[] role);

	void clearRole(String tokenId);

	void removeRole(String tokenId, String[] role);
	
	String[] getRoles(String tokenId);
}
