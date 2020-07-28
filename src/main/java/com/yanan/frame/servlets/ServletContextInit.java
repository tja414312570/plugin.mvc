package com.yanan.frame.servlets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.yanan.frame.plugin.Environment;
import com.yanan.frame.plugin.Plugin;
import com.yanan.frame.plugin.PlugsFactory;
import com.yanan.frame.plugin.annotations.Register;
import com.yanan.frame.plugin.decoder.ResourceDecoder;
import com.yanan.frame.plugin.definition.RegisterDefinition;
import com.yanan.utils.reflect.TypeToken;
import com.yanan.utils.resource.AbstractResourceEntry;
import com.yanan.utils.resource.Resource;
import com.yanan.utils.resource.ResourceManager;

/**
 * plugin mvc context initial class,
 * <p>support servlet context initial
 * @author yanan
 *
 */
@Register
public class ServletContextInit implements ServletContextListener{
	private final Logger log = LoggerFactory.getLogger( ServletContextInit.class);
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// add Plugin configure
		ServerContext serverContext = ServerContext.getContext();
		if(serverContext==null || sce == null ) {
			log.debug("None Web Server Env");
			return ;
		}
		main();
		serverContext.init(sce.getServletContext());
		log.debug("Web Servler Type:"+serverContext.getServerType().toString().toLowerCase());
		log.debug("Web server Info:"+sce.getServletContext().getServerInfo());
		String classPath = Thread.currentThread().getContextClassLoader()
				.getResource(".").getPath()
				.replace("%20", " ");
		System.out.println(classPath);
		ResourceManager.setClassPath(classPath,0);
		System.out.println(Arrays.toString(ResourceManager.classPaths()));
		System.out.println(ResourceManager.classPath());
		System.out.println(ResourceManager.getResource("classpath:plugin.yc"));
		Environment.getEnviroment().executorOnce(getClass(), ()->{
			PlugsFactory.init();
			String path = ServletContextInit.class.getResource("./conf/mvc.yc").getPath();
			Resource resource = ResourceManager.getResource(path);
			ResourceDecoder<Resource> resourceDecoder = PlugsFactory
					.getPluginsInstanceByAttributeStrict(
							new TypeToken<ResourceDecoder<Resource>>() {}.getTypeClass(),
							AbstractResourceEntry.class.getSimpleName());
			resourceDecoder.decodeResource(PlugsFactory.getInstance(), resource);
		});
		Config config = Environment.getEnviroment().getConfig("mvc");
		if(config!=null) {
			config.allowValueNull();
			Config server = config.getConfig("server");
			if(server !=null ) {
				server.allowKeyNull();
				List<? extends Object> webServletDispatcherList = server.getValueListUnwrapper(serverContext.getServerType().toString());
//				PlugsFactory.getInstance().addPlugsByConfigList(webServletDispatcherList);
//				PlugsFactory.getInstance().rebuild();
			}
		}
		
		Plugin plug = PlugsFactory.getPlugin(Servlet.class);
		List<RegisterDefinition> regList = plug.getRegisterList();
		log.debug("Web Server Dispatcher num:"+regList.size());
		log.debug("Dispatcher Type\tDispatcher Priority\tDispatcher Class");
		for(RegisterDefinition reg : regList) {
			log.debug(Arrays.toString(reg.getAttribute())+"\t"+reg.getPriority()+"\t"+reg.getRegisterClass().getName());
		}
		ServletBuilder.getInstance();
		ServletMapping servletManager = ServletMapping.getInstance();
		Map<String, ServletBean> servletMapping = servletManager.getServletMappingByStype("RESTFUL_STYLE");
		if (servletMapping == null) {
			log.debug("RESTFUL Servlet size:0");
			return;
		}
		log.debug("RESTFUL Servlet size:" + servletMapping.size());
		Iterator<Entry<String, ServletBean>> iterator = servletMapping.entrySet().iterator();
		log.debug("==============Traverse the servlet collection===========");
		while (iterator.hasNext()) {
			Entry<String, ServletBean> key = iterator.next();
			ServletBean servletBean = key.getValue();
			log.debug("---------------------------------------------------------");
			log.debug("url mapping:" + REQUEST_METHOD.getRequest(key.getKey()) + ",servlet method:"
					+ servletBean.getMethod());
			if (servletBean.getParameters() != null) {
				Iterator<Entry<Parameter, Map<Class<Annotation>, List<Annotation>>>> iterator1 = servletBean
						.getParameters().entrySet().iterator();
				while (iterator1.hasNext()) {
					Entry<Parameter, Map<Class<Annotation>, List<Annotation>>> e = iterator1.next();
					log.debug(e.getKey().toString());
					log.debug(e.getValue() + "");
				}
			}
			log.debug("------------------------------------------------");
		}
	}

	private void main() {
		ResourceManager.classPath();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}