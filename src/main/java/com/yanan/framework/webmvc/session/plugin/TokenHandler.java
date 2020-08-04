package com.yanan.framework.webmvc.session.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.annotations.Support;
import com.yanan.framework.plugin.definition.RegisterDefinition;
import com.yanan.framework.plugin.exception.PluginRuntimeException;
import com.yanan.framework.plugin.handler.FieldHandler;
import com.yanan.framework.plugin.handler.InstanceHandler;
import com.yanan.framework.plugin.handler.InvokeHandler;
import com.yanan.framework.plugin.handler.HandlerSet;
import com.yanan.framework.plugin.handler.MethodHandler;
import com.yanan.framework.webmvc.session.Token;
import com.yanan.framework.webmvc.session.annotation.Authentication;
import com.yanan.utils.reflect.cache.ClassHelper;
import com.yanan.utils.reflect.cache.MethodHelper;

@Support(Authentication.class)
@Register
public class TokenHandler implements InvokeHandler,FieldHandler,InstanceHandler {
	
	@Override
	public void before(RegisterDefinition registerDescription, Class<?> plugClass, Constructor<?> constructor,
			Object... args) {
		Authentication auth = ClassHelper.getClassHelper(plugClass).getConstructorHelper(constructor).getAnnotation(Authentication.class);
		if(auth==null)
			auth = ClassHelper.getClassHelper(plugClass).getAnnotation(Authentication.class);
		Token token = Token.getToken();
		if(token == null)
			throw new PermissionAuthException("Class "+plugClass +" instance need provide token");
		if(!token.containerRole(auth.roles()))
			throw new PermissionAuthException("No permission to instantiate the current class");
	}

	@Override
	public void after(RegisterDefinition registerDescription, Class<?> plugClass, Constructor<?> constructor,
			Object proxyObject, Object... args) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void before(MethodHandler methodHandler) {
		Authentication auth =MethodHelper.getMethodHelper(methodHandler.getMethod()).getAnnotation(Authentication.class);
		if(auth==null)
			auth = ClassHelper.getClassHelper(methodHandler.getPlugsProxy().getProxyClass()).getAnnotation(Authentication.class);
		Token token = Token.getToken();
		if(token == null)
			throw new PermissionAuthException("Method "+methodHandler.getMethod()+" invoke need provide token");
		if(!token.containerRole(auth.roles()))
			throw new PermissionAuthException("No permission to invoke method:"+methodHandler.getMethod());
	}

	@Override
	public void after(MethodHandler methodHandler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(MethodHandler methodHandler, Throwable e) {
	}

	@Override
	public void exception(RegisterDefinition registerDefinition, Class<?> plug, Constructor<?> constructor,
			Object proxyObject, PluginRuntimeException throwable, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preparedField(RegisterDefinition registerDefinition, Object proxy, Object target,
			HandlerSet handlerSet, Field field) {
		// TODO Auto-generated method stub
		
	}

}