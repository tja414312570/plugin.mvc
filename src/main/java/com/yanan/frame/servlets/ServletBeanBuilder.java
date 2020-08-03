package com.yanan.frame.servlets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yanan.frame.servlets.annotations.DeleteMapping;
import com.yanan.frame.servlets.annotations.GetMapping;
import com.yanan.frame.servlets.annotations.PostMapping;
import com.yanan.frame.servlets.annotations.PutMapping;
import com.yanan.frame.servlets.annotations.RequestMapping;

/**
 * 默认的ServletBean构造器
 * @author yanan
 */
public class ServletBeanBuilder implements ServletMappingBuilder{
	public static final String RESTFUL_STYLE="RESTFUL_STYLE";
	static Logger log = LoggerFactory.getLogger(ServletBeanBuilder.class);
	public static boolean builderRestful(RequestMapping requestMapping, Method method, RequestMapping parentRequestMaping, ServletMapping servletMannager){
		if(requestMapping.method().length==0)
			return true;
		for(int type : requestMapping.method()){
			try {
				ServletBean bean = builder(requestMapping.value(),method,parentRequestMaping==null?null:parentRequestMaping.value(),type);
				servletMannager.add(bean);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				continue;
			}
		}
		return true;
	}
	public static ServletBean builder(String requestMapping, Method method, String parentRequestMaping,int type) throws Exception {
		ServletBean bean = new ServletBean();
		bean.setStyle(RESTFUL_STYLE);
		bean.setRequestMethod(type);
		/**
		 * 获取url映射
		 */
		String urlPath;
		if(parentRequestMaping!=null){
			String namespace = parentRequestMaping.trim();
			if(namespace.equals(""))//如果父类命名空间为空时，父类命名空间为当前类名
				namespace = "/"+method.getDeclaringClass().getSimpleName();
			else if(namespace.equals("/"))//如果父类命名空间为/时，设置命名空间为空，因为子命名空间可能包含了/
				namespace="";
			if(requestMapping.trim().equals("")){
				urlPath = namespace;
			}else{
				urlPath = namespace+requestMapping.trim();
			}
		}else{
			urlPath = requestMapping.trim().equals("")?"/"+method.getName():requestMapping.trim();
		}
		String urlMapping = urlPath+"@"+type;
		bean.setUrlmapping(urlMapping);
		bean.setPathRegex(urlPath);
		bean.setMethod(method);
		bean.setServletClass(method.getDeclaringClass());
		/**
		 * 添加方法的参数,格式为参数类型，参数注解
		 */
		if (method.getParameterCount()!=0) {
			Parameter[] paras = method.getParameters();
			for(int i = 0;i<paras.length;i++)
				bean.addParameter(paras[i]);
		}
			return bean;
	}
	@Override
	public boolean builder(Class<? extends Annotation> annotationClass,Annotation annotation, Class<?> beanClass,Method beanMethod,
			ServletMapping servletMannager) {
		if(annotationClass.equals(RequestMapping.class)){//RequestMapping
			builderRestful((RequestMapping) annotation, beanMethod, beanClass.getAnnotation(RequestMapping.class), servletMannager);
			return true;
		}
		if(annotationClass.equals(GetMapping.class)){//GetMapping
			builderRestful((GetMapping) annotation, beanMethod, beanClass.getAnnotation(RequestMapping.class), servletMannager);
			return true;
		}
		if(annotationClass.equals(PutMapping.class)){//PutMapping
			builderRestful((PutMapping) annotation, beanMethod, beanClass.getAnnotation(RequestMapping.class), servletMannager);
			return true;
		}
		if(annotationClass.equals(PostMapping.class)){//PostMapping
			builderRestful((PostMapping) annotation, beanMethod, beanClass.getAnnotation(RequestMapping.class), servletMannager);
			return true;
		}
		if(annotationClass.equals(DeleteMapping.class)){//DeleteMapping
			builderRestful((DeleteMapping) annotation, beanMethod, beanClass.getAnnotation(RequestMapping.class), servletMannager);
			return true;
		}
		return false;
	}
	private void builderRestful(DeleteMapping deleteMapping, Method method, RequestMapping requestMapping,
			ServletMapping servletMannager) {
			try {
				ServletBean bean = builder(deleteMapping.value(),method,requestMapping==null?null:requestMapping.value(),REQUEST_METHOD.DELETE);
				servletMannager.add(bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}

	private void builderRestful(GetMapping mapping, Method method, RequestMapping requestMapping,
			ServletMapping servletMannager) {
			try {
				ServletBean bean = builder(mapping.value(),method,requestMapping==null?null:requestMapping.value(),REQUEST_METHOD.GET);
				servletMannager.add(bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}

	private void builderRestful(PutMapping mapping, Method method, RequestMapping requestMapping,
			ServletMapping servletMannager) {
			try {
				ServletBean bean = builder(mapping.value(),method,requestMapping==null?null:requestMapping.value(),REQUEST_METHOD.PUT);
				servletMannager.add(bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}

	private void builderRestful(PostMapping mapping, Method method, RequestMapping requestMapping,
			ServletMapping servletMannager) {
			try {
				ServletBean bean = builder(mapping.value(),method,requestMapping==null?null:requestMapping.value(),REQUEST_METHOD.POST);
				servletMannager.add(bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}

}