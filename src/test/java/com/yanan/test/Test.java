package com.yanan.test;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;

import com.yanan.frame.plugin.annotations.Register;
import com.yanan.frame.plugin.annotations.Service;
import com.yanan.frame.servlets.ServletContextInit;
import com.yanan.frame.servlets.annotations.RequestMapping;
import com.yanan.frame.servlets.response.annotations.ResponseJson;
import com.yanan.frame.servlets.validator.annotations.FastValidate;
import com.yanan.frame.servlets.validator.annotations.Length;
import com.yanan.frame.servlets.validator.annotations.Validate;
import com.yanan.frame.plugin.PlugsFactory;

@FastValidate
@Validate
@Register(signlTon=true)
public class Test {
	@NotNull
	private String name ="";
	@Service
	@Length(min=1)
	private Logger log;
	@ResponseJson
	@RequestMapping("/testApi")
//	
	public String test(@NotNull @Length(min=1) String name ,@Min(2) int age ,@NotNull @Validate Test test) {
		System.out.println(name+"  "+age);
		return name;
	}
	public static void main(String[] args) {
		PlugsFactory.getInstance().addScanPath("/Volumes/GENERAL/git/plugin.mvc/target/classes");
		ServletContextInit in = PlugsFactory.getPluginsInstance(ServletContextInit.class);
		in.contextInitialized(null);
		Test init = PlugsFactory.getPluginsInstance(Test.class);
		try {
			init.test(null,3,init);
			init.test("word",2,null);
		}catch(com.yanan.frame.servlets.validator.ParameterVerificationFailed e) {
			e.printStackTrace();
			Map v = e.getValidResult();
			System.out.println(v);
		}
		
		
	}
}