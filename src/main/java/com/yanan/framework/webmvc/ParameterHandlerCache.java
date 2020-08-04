package com.yanan.framework.webmvc;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.builder.PluginInstanceFactory;
import com.yanan.framework.plugin.definition.RegisterDefinition;
import com.yanan.framework.webmvc.parameter.ParameterHandler;

/**
 * 参数Handler缓存，用于缓存参数处理器
 * 关于参数处理器{@link com.yanan.framework.webmvc.paramete.ParameterHandler}
 * @author yanan
 *
 */
public class ParameterHandlerCache {
	private Map<RegisterDefinition,ParameterHandler> parameterHandlerCache = new HashMap<RegisterDefinition,ParameterHandler>();
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletBean servletBean;
	public ParameterHandlerCache(HttpServletRequest request, HttpServletResponse response, ServletBean servletBean) {
		super();
		this.request = request;
		this.response = response;
		this.servletBean = servletBean;
	}
	public ParameterHandler getParameterHandler(String attribute) throws Exception{
		RegisterDefinition regsiter = PlugsFactory.getPlugin(ParameterHandler.class).getRegisterDefinitionByAttributeStrict(attribute);
		if(regsiter==null)//如果没有得到RegisterDescription ,返回null
			return null;
		//从parameterHandlerCache中查询是否有该 RegisterDescrptiong
		ParameterHandler parameterHandler = parameterHandlerCache.get(regsiter);
		//如果parameterHandlerCache中没有该RegsiterDescription的实例，则新建一个,此时一定会拿到ParameterHandler不过可能存在由于实例化的异常
		if(parameterHandler==null){
			parameterHandler=PluginInstanceFactory.getRegisterInstance(regsiter,ParameterHandler.class);
			parameterHandler.initHandler(request, response, servletBean,this);
			parameterHandlerCache.put(regsiter, parameterHandler);
		}
		return parameterHandler;
	}
	public ParameterHandler getParameterHandler(Annotation parameterAnnotation) throws Exception {
		return this.getParameterHandler(parameterAnnotation.annotationType().getName());
	}
	public ParameterHandler getParameterHandler(Class<?> parameterType) throws Exception {
		return this.getParameterHandler(parameterType.getName());
	}
}