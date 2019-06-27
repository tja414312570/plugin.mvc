package com.YaNan.test;
import javax.servlet.ServletContext;

import org.slf4j.Logger;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.servlets.ServletContextInit;
import com.YaNan.frame.servlets.annotations.RequestMapping;
import com.YaNan.frame.servlets.response.annotations.ResponseJson;

@Register(method="test",signlTon=true)
public class Test {
	private String name ;
	@Service
	private Logger log;
	@ResponseJson
	@RequestMapping("/testApi")
	public String test() {
		name = "nasme1";
		log.debug("注入日志："+name);
		return name;
	}
	public static void main(String[] args) {
		ServletContextInit init = PlugsFactory.getPlugsInstance(ServletContextInit.class);
		init.contextInitialized(null);
	}
}
