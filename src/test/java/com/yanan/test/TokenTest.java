package com.yanan.test;

import java.lang.reflect.InvocationTargetException;

import com.yanan.framework.token.Token;
import com.yanan.framework.token.TokenBuilderFactory;
import com.yanan.framework.token.TokenManager;
import com.yanan.framework.token.annotation.Authentication;

@Authentication
public class TokenTest {
	@Authentication
	public void test() {
		System.out.println("输出");
	}
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InvocationTargetException, NoSuchMethodException, InterruptedException {
		TokenManager tokenManager = TokenBuilderFactory.builder("classpath:token.yc");
		System.out.println(tokenManager);
		Token token = Token.getToken();
		token.addRole("root");
		token.set(Token.class, "测试数据");
		Thread thread  = new Thread(()->{
			while(true) {
				System.out.println("----------------------------------------------------------");
				System.out.println(token.getId()+"===>"+token.get(Token.class));
				token.requiredRole("root");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
//		System.out.println(thread);
		//获取上线文管理
//		TokenManager tokenManager = TokenManager.getInstance();
//		tokenManager.build("classpath:plugin.yc");
//		Token token = Token.getToken();
//		System.out.println(token.isRole("role"));
//		token.addRole("root");
//		token.requiredRole("root");
//		token.requiredPermission("list:edit");
//		token.addRole(TokenTest.class);
//		PlugsFactory.init();
//		TokenTest tokenTest = PlugsFactory.getPluginsInstance(TokenTest.class);
//		tokenTest.test();
	}
}
