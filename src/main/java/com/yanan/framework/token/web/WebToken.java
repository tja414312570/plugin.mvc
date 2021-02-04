package com.yanan.framework.token.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yanan.framework.token.Token;
import com.yanan.framework.token.TokenPool;

public class WebToken {
	/**
	 * 从request中获取token id
	 * @param requestContext
	 * @return
	 */
	public static String getTokenId(HttpServletRequest requestContext) {
		Cookie[] cookies = requestContext.getCookies();
		if (cookies == null||cookies.length==0) {
			return requestContext.getParameter(WebTokenManager.getInstance().tokenMark);
		} else {
			for(int i = 0;i<cookies.length;i++){
				if(cookies[i].getName().equals(WebTokenManager.getInstance().tokenMark))
					return cookies[i].getValue();
			}
		}
		return null;
	}
	/**
	 * 获取token
	 * @param requestContext
	 * @return
	 */
	public static Token getToken(HttpServletRequest requestContext){
		Token token = TokenPool.getToken(getTokenId(requestContext));
		if(token==null)
			token = TokenPool.getToken((String)requestContext.getAttribute(WebTokenManager.getInstance().tokenMark));
		return token;
	}
	/**
	 * 新增token
	 * @param request
	 * @param response
	 */
	public static Token addToken(HttpServletRequest request,HttpServletResponse response){
		Token token = Token.getToken();
		String tokenId = token.getId();
		writeCookie(request, response);
		request.setAttribute(WebTokenManager.getInstance().tokenMark, tokenId);
		return token;
	}
	private static void writeCookie(HttpServletRequest request,HttpServletResponse response) {
		if(request==null||response==null)
			throw new RuntimeException("No servlet context!");
		Token token = Token.getToken();
		Cookie cookie = new Cookie(WebTokenManager.getInstance().tokenMark,token.getId());
		String path = WebTokenManager.getInstance().path;
		if(path==null)
			path = request.getContextPath().length()==0?"/":request.getContextPath();
		cookie.setPath(path);
//		cookie.setMaxAge(token.getTimeOut());
		cookie.setHttpOnly(WebTokenManager.getInstance().httpOnly);
		cookie.setSecure(WebTokenManager.getInstance().secure);//启用时 非 https 不能获取到ID
		response.addCookie(cookie);
	}
}
