package com.yanan.framework.webmvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 请求上下文<br>
 * 仅在请求开始且未结束时有效
 * @author yanan
 *
 */
public final class RequestContext {
	private ServletBean servletBean;
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	private Thread currentThread;
	private String contextId;
	private static ConcurrentHashMap<String,RequestContext> requestContextMap = new ConcurrentHashMap<String,RequestContext>(16);
	private static InheritableThreadLocal<RequestContext> threadRequestContext = new InheritableThreadLocal<RequestContext>();
	/**
	 * 设置当前线程的强求上下文
	 * @param servletBean
	 * @param request
	 * @param response
	 * @return
	 */
	static RequestContext setCurrentRequestContext(ServletBean servletBean,HttpServletRequest request,HttpServletResponse response) {
		RequestContext context = getCurrentThreadContext();
		RequestContext requestContext = new RequestContext();
		requestContext.setHttpServletRequest(request);
		requestContext.setHttpServletResponse(response);
		requestContext.setServletBean(servletBean);
		requestContext.setCurrentThread(Thread.currentThread());
		requestContext.setContextId(UUID.randomUUID().toString());
		threadRequestContext.set(requestContext);
		requestContextMap.put(requestContext.getContextId(), requestContext);
		return context;
	}
	/**
	 * 获取当前线程的请求上下文
	 * @return
	 */
	public static RequestContext getCurrentThreadContext() {
		return threadRequestContext.get();
	}
	public static Map<String,RequestContext> getAllRequestContext(){
		return new HashMap<String,RequestContext>(requestContextMap);
	}
	/**
	 * 移除当前线程的请求上下文
	 * @return
	 */
	static RequestContext remvoeCurrentContxt() {
		RequestContext context = getCurrentThreadContext();
		if(context != null)
			requestContextMap.remove(context.getContextId());
		threadRequestContext.remove();
		return context;
	}
	public ServletBean getServletBean() {
		return servletBean;
	}
	void setServletBean(ServletBean servletBean) {
		this.servletBean = servletBean;
	}
	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}
	void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}
	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}
	void setHttpServletResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
	}
	public Thread getCurrentThread() {
		return currentThread;
	}
	void setCurrentThread(Thread currentThread) {
		this.currentThread = currentThread;
	}
	public String getContextId() {
		return contextId;
	}
	void setContextId(String contextId) {
		this.contextId = contextId;
	}
}