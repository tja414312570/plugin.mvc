package com.yanan.framework.webmvc;

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
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.SimpleConfigObject;
import com.yanan.framework.plugin.Environment;
import com.yanan.framework.plugin.Plugin;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.builder.PluginDefinitionBuilderFactory;
import com.yanan.framework.plugin.decoder.ResourceDecoder;
import com.yanan.framework.plugin.definition.RegisterDefinition;
import com.yanan.framework.webmvc.exception.MVCContextInitException;
import com.yanan.utils.CollectionUtils;
import com.yanan.utils.asserts.Assert;
import com.yanan.utils.reflect.AppClassLoader;
import com.yanan.utils.reflect.TypeToken;
import com.yanan.utils.resource.ClassPathResource;
import com.yanan.utils.resource.Resource;
import com.yanan.utils.resource.ResourceManager;
import com.yanan.utils.string.StringUtil;

/**
 * mvc环境初始化
 * <p>此类用于提供mvc上下文环境的初始化
 * @author yanan
 *
 */
@Register
public class ServletContextInit implements ServletContextListener{
	//日志
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * WEB环境启动时调用此方法初始化MVC环境
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// 获取服务上下文
		ServerContext serverContext = ServerContext.getContext();
		//判断是否WEB环境
		Assert.isTrue(serverContext==null || sce == null,new MVCContextInitException("None Web Server Env"));
		//将ServerContext环境设置为当前上下环境
		serverContext.init(sce.getServletContext());
		//打印一些信息
		logger.info("Web server Type:"+serverContext.getServerType().toString().toLowerCase());
		logger.debug("Web server Info:"+sce.getServletContext().getServerInfo());
		//设置当前类路径
		//因为web环境和普通环境不一样，需要独立处理类路径资源
		//设置当前主类路径为当前线程上下文的加载的类路径
		ResourceManager.setClassPath(AppClassLoader.getCurrentContextClassPath(),0);
		//判断Plugin环境是否初始化，没有初始化就初始化
		Environment.getEnviroment().executeOnlyOnce(getClass(), ()->{
			//初始化Plugin
			PlugsFactory.init();
			//获取mvc配置文件路径
			String path = ResourceManager.processPath(ServletContextInit.class.
					getResource("./conf/mvc.yc").getPath().replace("file:", ""));
			//获取资源
			Resource resource = ResourceManager.getResource(path);
			//获取资源解析器
			ResourceDecoder<Resource> resourceDecoder = PlugsFactory
					.getPluginsInstanceByAttributeStrict(
							new TypeToken<ResourceDecoder<Resource>>() {}.getTypeClass(), ClassPathResource.class.getSimpleName());
			//解析资源
			resourceDecoder.decodeResource(PlugsFactory.getInstance(), resource);
		});
		//判断mvc配置是否存在
		Config config = Environment.getEnviroment().getConfig("mvc");
		if(config != null) {
			//将配置添加到上下文环境
			serverContext.setConfig(config);
			//为了方便调用，允许key和value为null，否则一堆因配置不存在引起的错误
			config.allowKeyNull();
			config.allowValueNull();
		}
		//推断出当前环境的服务调配器
		deduceWebServletDispatcher(serverContext);
		//打印调配器信息
		loggerDispatcherInfo();
		//获取ServletBuilder实例并构建
		ServletBuilder.getInstance().init();;
		//打印ServletBean信息
		loggerServertBeanInfo();
	}
	private void loggerServertBeanInfo() {
		ServletMapping servletManager = ServletMapping.getInstance();
		Map<String, ServletBean> servletMapping = servletManager.getServletMappingByStype("RESTFUL_STYLE");
		if (CollectionUtils.isEmpty(servletMapping)) {
			logger.debug("RESTFUL Servlet size:0");
			return;
		}
		logger.debug("RESTFUL Servlet size:" + servletMapping.size());
		Iterator<Entry<String, ServletBean>> iterator = servletMapping.entrySet().iterator();
		logger.debug("==============Traverse the servlet collection===========");
		while (iterator.hasNext()) {
			Entry<String, ServletBean> key = iterator.next();
			ServletBean servletBean = key.getValue();
			logger.debug("---------------------------------------------------------");
			logger.debug("url mapping:" + REQUEST_METHOD.getRequest(key.getKey()) + ",servlet method:"
					+ servletBean.getMethod());
			if (servletBean.getParameters() != null) {
				Iterator<Entry<Parameter, Map<Class<Annotation>, List<Annotation>>>> iterator1 = servletBean
						.getParameters().entrySet().iterator();
				while (iterator1.hasNext()) {
					Entry<Parameter, Map<Class<Annotation>, List<Annotation>>> e = iterator1.next();
					logger.debug(e.getKey().toString());
					logger.debug(e.getValue() + "");
				}
			}
			logger.debug("------------------------------------------------");
		}
	}
	//打印调配器信息
	private void loggerDispatcherInfo() {
		//获取所有注册的调配器，因为调配器都实现了Servlet接口
		Plugin plugin = PlugsFactory.getPlugin(Servlet.class);
		List<RegisterDefinition> regList = plugin.getRegisterList();
		logger.info("Web server dispatcher num:"+regList.size());
		logger.info("dispatcher type\tdispatcher priority\tdispatcher class");
		for(RegisterDefinition reg : regList) {
			logger.info(Arrays.toString(reg.getAttribute())+"\t"+reg.getPriority()+"\t"+reg.getRegisterClass().getName());
		}
	}
	//推断出环境的服务调配器
	private void deduceWebServletDispatcher(ServerContext serverContext) {
		//没有配置//跳过
		if(serverContext.getConfig() == null)
			return;
		//获取服务配置
		Config server = serverContext.getConfig()
				.getConfig(Config_MVC_Constant.MVC_SERVER);
		if(server == null)
			return ;
		server.allowKeyNull();
		//根据服务类型获取调配器
		String serverType = serverContext.getServerType().toString().toLowerCase();
		//获取配置列表
		ConfigList webServletDispatcherList = server.getList(serverType);
		
		if(CollectionUtils.isEmpty(webServletDispatcherList))
			return ;
		//遍历添加到Plugin环境 //早知道写个自动的方法
		webServletDispatcherList.forEach(configValue->{
			RegisterDefinition definition;
			//判断配置，并且构建组件定义
			if(configValue.valueType() == ConfigValueType.STRING) {
				Class<?> dispatcherClass;
				try {
					dispatcherClass = Class.forName((String) configValue.unwrapped());
					definition =  PluginDefinitionBuilderFactory.builderRegisterDefinition(dispatcherClass);
				} catch (ClassNotFoundException e) {
					throw new MVCContextInitException("failed to add servlet dispatcher for " + configValue.unwrapped(), e);
				}
			}else {
				definition =  PluginDefinitionBuilderFactory
						.buildRegisterDefinitionByConfig(((SimpleConfigObject)configValue).toConfig());
			}
			//将组件定义添加到Plugin
			PlugsFactory.getInstance().addRegisterDefinition(definition);
		});
	}
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}