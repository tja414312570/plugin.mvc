package com.yanan.framework.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.yanan.framework.token.exception.PermissionAuthException;
import com.yanan.framework.token.exception.RoleAuthException;
/**
 * 令牌
 * @author Administrator
 * @version 101
 */
public class Token {
	//令牌id
	private String tokenId;
	//参数列表
	private Map<String, Object> arguments = new HashMap<String, Object>();
	//角色列表
	private volatile Set<String> roleSet = new HashSet<>();
	//	权限列表
	private volatile Set<String> permissionSet = new HashSet<>();
	//当前请求token线程
	private Thread requiredThread;
	//最后更新时间
	private long lastuse;
	/**
	 * 设置token超时，以分钟计算
	 * @param maxTimeOut
	 */
	/*******************************存储*************************/
	public boolean container(Class<?> cls) {
		return this.arguments.containsKey(cls.getName());
	}
	public boolean container(String key){
		return this.arguments.containsKey(key);
	}
	/**
	 * Delete all data, including persistent data
	 */
	public void clearAll(){
		this.arguments.clear();
	}
	public void clear(){
		this.arguments.clear();
	}
	public void set(String key, Object value) {
		this.arguments.put(key, value);
	}
	public void set(Class<?> cls, Object value) {
		this.arguments.put(cls.getName(), value);
	}
	public Object get(String key) {
		return arguments.get(key);
	}
	public void remove(String key){
		this.arguments.remove(key);
	}
	public void set(Object obj) {
		this.arguments.put(obj.getClass().getName(), obj);
	}
	public Object get(Class<?> cls) {
		return this.arguments.get(cls.getName());
	}
	public Set<Entry<String, Object>> attributeEntry() {
		return this.arguments.entrySet();
	}
	public void remove(Class<?> cls) {
		this.arguments.remove(cls.getName());
	}
	/***********************************存储结束*****************/
	private Token() {
		this.tokenId = newTokenId();
		this.refresh();
	}
	public String getId() {
		return tokenId;
	}
	/**
	 * 从池中获取token
	 * @return token
	 */
	public static Token getToken(){
		Token token = TokenPool.getToken();
		if(token == null) {
			token = new Token();
			TokenPool.setToken(token);
		}
		return token;
	}
	public static void delete(){
		TokenPool.deleteToken();
	}
	/**
	 * 产生新的token Id
	 * @return
	 */
	private static String newTokenId() {
		UUID uuid = UUID.randomUUID();
		return new StringBuilder(32).append(digits(uuid.getMostSignificantBits() >> 32, 8))
				.append(digits(uuid.getMostSignificantBits() >> 16, 4))
				.append(digits(uuid.getMostSignificantBits(), 4))
				.append(digits(uuid.getLeastSignificantBits() >> 48, 4))
				.append(digits(uuid.getLeastSignificantBits(), 12)).toString();
	}
	/**
	 * 获取角色
	 * @return 角色集合
	 */
	public Set<String> getRoles() {
		return roleSet;
	}
	/**
	 * 匹配角色
	 * @param roles
	 * @return 角色集合
	 */
	public String[] matchRoles(String[] roles) {
		List<String> rolesList = new ArrayList<String>();
		for (String role : roles) {
			if (roleSet.contains(role))
				rolesList.add(role);
			}
		return rolesList.toArray(new String[rolesList.size()]);
	}
	/**
	 * 是否包含角色中某个角色
	 * @param roles
	 * @return
	 */
	public boolean containerRole(String[] roles) {
		for (String role : roles) {
			if(isRole(role)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 是否是某角色
	 * @param role
	 * @return
	 */
	public boolean isRole(Class<?> role){
		return isRole(role.getSimpleName());
	}
	/**
	 * 是否是某角色
	 * @param role
	 * @return
	 */
	public boolean isRole(String role) {
		return roleSet.contains(role);
	}
	/**
	 * 删除角色
	 * @param roles
	 */
	public void delRoles(Class<?>... roles){
		for(Class<?> role : roles){
			this.delRole(role);
		}
	}
	/**
	 * 删除角色
	 * @param role
	 */
	public void delRole(Class<?> role){
		this.delRole(role.getSimpleName());
	}
	/**
	 * 删除角色
	 * @param roles
	 */
	public void delRole(String... roles){
		for(String role : roles){
			this.delRole(role);
		}
	}
	/**
	 * 删除角色
	 * @param role
	 */
	public synchronized void delRole(String role) {
		roleSet.remove(role);
	}
	/**
	 * 添加角色
	 * @param roles
	 */
	public void addRoles(Class<?>... roles){
		for(Class<?> role : roles){
			this.addRole(role);
		}
	}
	/**
	 * 添加角色
	 * @param role
	 */
	public void addRole(Class<?> role){
		this.addRole(role.getSimpleName());
	}
	/**
	 * 添加角色
	 * @param roles
	 */
	public void addRoles(String... roles){
		for(String role : roles){
			this.addRole(role);
		}
	}
	/**
	 * 添加角色
	 * @param role
	 */
	public synchronized void addRole(String role) {
		this.roleSet.add(role);
	}
	/**
	 * 删除所有角色
	 */
	public synchronized void delAllRole(){
		this.roleSet.clear();
	}

	/**
	 * 销毁token
	 */
	public void destory(){
		TokenPool.deleteToken(this.tokenId);
	}
	void clearToken() {
		this.arguments.clear();
		this.roleSet.clear();
		this.permissionSet.clear();
	}
	public static String digits(long val, int digits) {
		long hi = 1L << (digits * 4);
		return Long.toHexString(hi | (val & (hi - 1))).substring(1);
	}
	public long getLastuse() {
		return lastuse;
	}
	public void setLastuse(long lastuse) {
		this.lastuse = lastuse;
	}
	//请求角色
	public void requiredRole(String role) {
		if(!this.roleSet.contains(role.intern())) {
			throw new RoleAuthException("the requierd role ["+role+"] is not found");
		}
	}
	/**
	 * 请求权限
	 * @param permission 权限
	 */
	public void requiredPermission(String permission) {
		if(!this.permissionSet.contains(permission.intern())) {
			throw new PermissionAuthException("the requierd permission ["+permission+"] is not found");
		}
	}
	public Thread getRequiredThread() {
		return requiredThread;
	}
	public void refresh() {
		this.lastuse = System.currentTimeMillis();
		this.requiredThread = Thread.currentThread();
	}
}