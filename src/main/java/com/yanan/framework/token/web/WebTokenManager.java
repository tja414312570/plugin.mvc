package com.yanan.framework.token.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yanan.framework.token.Token;
import com.yanan.framework.token.TokenManager;
import com.yanan.framework.token.entity.TokenEntity;
import com.yanan.utils.string.PathMatcher;

public class WebTokenManager extends TokenManager{
	public String tokenMark = "TUID"; //Token标记
	public String path;//cookie有效域
	public boolean secure = false;//启用secure
	public boolean httpOnly = true;//启用HttpOnly
	private Map<String, TokenEntity> tokenMap = new LinkedHashMap<String, TokenEntity>();
	
	public static WebTokenManager getInstance() {
		return WebTokenManagerHolder.instance;
	}
	private static class WebTokenManagerHolder{
		static WebTokenManager instance = new WebTokenManager();
	}
	public Map<String, TokenEntity> getTokenMap() {
		return tokenMap;
	}

	public boolean match(String namespace) {
		Iterator<String> iterator =tokenMap.keySet().iterator();
		while(iterator.hasNext()){
			if(PathMatcher.match(iterator.next(), namespace).isMatch())
				return true;
		}
		return false;
	}

	public List<TokenEntity> getTokenEntitys(String url) {
		List<TokenEntity> tl= new ArrayList<TokenEntity>();
		Iterator<String> iterator = tokenMap.keySet().iterator();
		while(iterator.hasNext()){
			String nameReg =  iterator.next();
			if(PathMatcher.match(nameReg, url).isMatch())
				tl.add(tokenMap.get(nameReg));
		}
		return tl;
	}
	
	public Token getToken(HttpServletRequest requestContext){
		Token token = getToken(getTokenId(requestContext));
			if(token==null)
				token = TokenManager.getToken((String)requestContext.getAttribute(tokenMark));
		return token;
	}
	/**
	 * 从request中获取token id
	 * @param requestContext
	 * @return
	 */
	public String getTokenId(HttpServletRequest requestContext) {
		Cookie[] cookies = requestContext.getCookies();
		if (cookies == null||cookies.length == 0) {
			return requestContext.getParameter(tokenMark);
		} else {
			for(int i = 0;i<cookies.length;i++){
				if(cookies[i].getName().equals(tokenMark))
					return cookies[i].getValue();
			}
		}
		return null;
	}
	public void writeCookie(HttpServletRequest request,HttpServletResponse response,Token token) {
		request.setAttribute(tokenMark, token.getId());
		if(request==null || response==null)
			throw new RuntimeException("No servlet context!");
		Cookie cookie = new Cookie(tokenMark,token.getId());
		if(path==null)
			path = request.getContextPath().length()==0?"/":request.getContextPath();
		cookie.setPath(path);
//		cookie.setMaxAge(TokenManager.getInstance().getTimeout());
		cookie.setHttpOnly(httpOnly);
		cookie.setSecure(secure);//启用时 非 https 不能获取到ID
		response.addCookie(cookie);
	}
}