package com.YaNan.frame.servlets;

import java.io.InputStream;
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

import com.YaNan.frame.plugin.ConfigContext;
import com.YaNan.frame.plugin.Plug;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.PlugsFactory.STREAM_TYPT;
import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.annotations.Register;
import com.typesafe.config.Config;

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
		serverContext.init(sce.getServletContext());
		log.debug("Web Servler Type:"+serverContext.getServerType().toString().toLowerCase());
		log.debug("Web server Info:"+sce.getServletContext().getServerInfo());
		InputStream	pluginConf = ServletContextInit.class.getResourceAsStream("./conf/plugin.conf");
		PlugsFactory.getInstance().addPlugs(pluginConf,STREAM_TYPT.CONF,null);
		Config config = ConfigContext.getInstance().getGlobalConfig().getConfig("MVC");
		if(config!=null) {
			config.allowValueNull();
			Config server = config.getConfig("SERVER");
			if(server !=null ) {
				server.allowKeyNull();
				List<? extends Object> webServletDispatcherList = server.getValueListUnwrapper(serverContext.getServerType().toString());
				PlugsFactory.getInstance().addPlugsByConfigList(webServletDispatcherList);
				PlugsFactory.getInstance().rebuild();
			}
		}
		
		Plug plug = PlugsFactory.getPlug(Servlet.class);
		List<RegisterDescription> regList = plug.getRegisterList();
		log.debug("Web Server Dispatcher num:"+regList.size());
		log.debug("Dispatcher Type\tDispatcher Priority\tDispatcher Class");
		for(RegisterDescription reg : regList) {
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

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}
