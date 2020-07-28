package com.yanan.frame.servlets.session.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.yanan.frame.plugin.annotations.Register;
import com.yanan.frame.plugin.annotations.Support;
import com.yanan.frame.plugin.definition.RegisterDefinition;
import com.yanan.frame.plugin.exception.PluginRuntimeException;
import com.yanan.frame.plugin.handler.FieldHandler;
import com.yanan.frame.plugin.handler.InstanceHandler;
import com.yanan.frame.plugin.handler.InvokeHandler;
import com.yanan.frame.plugin.handler.InvokeHandlerSet;
import com.yanan.frame.plugin.handler.MethodHandler;
import com.yanan.frame.servlets.session.Token;
import com.yanan.frame.servlets.session.annotation.Authentication;
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
			InvokeHandlerSet handlerSet, Field field) {
		// TODO Auto-generated method stub
		
	}

}