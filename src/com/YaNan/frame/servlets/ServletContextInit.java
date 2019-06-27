package com.YaNan.frame.servlets;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.PlugsFactory.STREAM_TYPT;
import com.YaNan.frame.plugin.annotations.Register;

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
		InputStream	pluginConf = ServletContextInit.class.getResourceAsStream("./conf/plugin.conf");
		PlugsFactory.getInstance().addPlugs(pluginConf,STREAM_TYPT.CONF,null);
		ServletBuilder.getInstance();
		ServletMapping servletManager = ServletMapping.getInstance();
		Map<String, ServletBean> servletMapping = servletManager.getServletMappingByStype("RESTFUL_STYLE");
		if (servletMapping == null) {
			log.debug("RESTFUL Servlet num:0");
			return;
		}
		log.debug("RESTFUL Servlet num:" + servletMapping.size());
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
