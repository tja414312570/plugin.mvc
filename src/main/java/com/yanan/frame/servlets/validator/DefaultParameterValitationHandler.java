package com.yanan.frame.servlets.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintViolation;

import com.yanan.frame.plugin.PlugsFactory;
import com.yanan.frame.plugin.annotations.Register;
import com.yanan.frame.plugin.handler.InvokeHandler;
import com.yanan.frame.plugin.handler.MethodHandler;
import com.yanan.frame.servlets.annotations.Groups;
import com.yanan.frame.servlets.validator.annotations.FastValidate;
import com.yanan.frame.servlets.validator.annotations.Validate;
import com.yanan.utils.reflect.cache.ClassHelper;
/**
 * jsr 303 标准验证拦截
 * 最后调用此验证器
 * @author yanan
 */
@Register(attribute="*",priority=Integer.MAX_VALUE)
public class DefaultParameterValitationHandler implements InvokeHandler{
	Set<Method> methodIgnoreList = new HashSet<Method>();
	Set<Method> resultIgnoreList = new HashSet<Method>();
	@Override
	public void before(MethodHandler methodHandler) {
		//获取所有参数
		Object[] parameters = methodHandler.getParameters();
		//获取要验证的方法
		Method method = methodHandler.getMethod();
		//判断方法是否需要验证
		if(!methodIgnoreList.contains(method)) {
			boolean ignore = true;
			//获取参数
			if(parameters ==null || parameters.length==0) {
				methodIgnoreList.add(method);
				return;
			}
			List<Annotation> jsrAnnoList = null;
			Class<?>[] groups;
			//获取验证分组
			Groups groupsAnno = method.getAnnotation(Groups.class);
			if(groupsAnno == null) 
				groupsAnno = methodHandler.getPlugsProxy().getRegisterDefinition().getRegisterClass().getAnnotation(Groups.class);
			//是否为快速认证
			boolean fastValidate = method.getAnnotation(FastValidate.class)!=null;
			if(!fastValidate)
				fastValidate = methodHandler.getPlugsProxy().getRegisterDefinition().getRegisterClass().getAnnotation(FastValidate.class)!=null;
			groups = groupsAnno == null? null:groupsAnno.value();
			Map<Object,ConstraintViolation<?>> validResult = new HashMap<Object,ConstraintViolation<?>>();
			boolean needParameterValid = true;
			if(method.getDeclaringClass().getAnnotation(Validate.class)!=null) {
				ignore = false;
				if(!valitatiy(methodHandler.getPlugsProxy().getProxyObject().getClass(), methodHandler.getPlugsProxy().getProxyObject(), fastValidate, validResult, groups)) {
					if(fastValidate)
						needParameterValid = false;
				}
			}
			
			//遍历参数
paramsLoop:	for(int i = 0;i<parameters.length && needParameterValid;i++) {
				Parameter parameter = method.getParameters()[i];
				Object value = parameters[i];
				//取得验证注解
				jsrAnnoList = PlugsFactory.getAnnotationGroup(parameter, Constraint.class);
				if(jsrAnnoList!=null) {
					ignore = false;
					for(Annotation anno : jsrAnnoList){
						ParameterValidator parameterValidator = PlugsFactory.getPluginsInstanceByAttributeStrict(ParameterValidator.class, anno.annotationType().getName());
						ConstraintViolation<?> valitation = parameterValidator.validate(parameter,anno,value,groups);
						if(valitation!=null){
							validResult.put(parameter, valitation);
							if(fastValidate)
								break paramsLoop;
							continue paramsLoop;
						}
					}
				}
				boolean pojo = parameter.getAnnotation(Validate.class) != null;
				if(pojo) {
					ignore = false;
					if(!valitatiy(parameter.getType(),value,fastValidate,validResult,groups)) {
						if(fastValidate)
							break paramsLoop;
					}
				}
			}
			if(ignore) 
				methodIgnoreList.add(method);
			if(!validResult.isEmpty())
				throw new ParameterVerificationFailed(validResult);
		}
	}
	private boolean valitatiy(Class<?> beanType, Object bean, boolean fastValidate,
			Map<Object, ConstraintViolation<?>> validResult, Class<?>[] groups) {
		//获取参数类型
		List<Annotation> jsrAnnoList = null;
		//获取bean类
		for(Field field : ClassHelper.getClassHelper(beanType).getAllFields()) {
			jsrAnnoList = PlugsFactory.getAnnotationGroup(field, Constraint.class);
			if(jsrAnnoList!=null) {
				field.setAccessible(true);
				Object value;
				try {
					value = field.get(bean);
					field.setAccessible(false);
					for(Annotation anno : jsrAnnoList){
						ParameterValidator parameterValidator = PlugsFactory.getPluginsInstanceByAttributeStrict(ParameterValidator.class, anno.annotationType().getName());
						ConstraintViolation<?> valitation = parameterValidator.validate(field,anno,value,groups);
						if(valitation!=null){
							validResult.put(field, valitation);
							if(fastValidate)
								return false;
							break;
						}
					}
					boolean pojo = field.getAnnotation(Validate.class) != null;
					if(pojo) {
						if(!valitatiy(field.getType(),value,fastValidate,validResult,groups)) {
							if(fastValidate)
								return false;
						}
					}
				} catch (Exception e) {
					throw new ParameterVerificationException(e);
				}
			}
			
		}
		return true;
	}

	@Override
	public void after(MethodHandler methodHandler) {
	}

	@Override
	public void error(MethodHandler methodHandler, Throwable e) {
	}

}