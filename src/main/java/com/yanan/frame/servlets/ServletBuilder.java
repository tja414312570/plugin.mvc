package com.yanan.frame.servlets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import com.yanan.frame.plugin.Environment;
import com.yanan.frame.plugin.PlugsFactory;
import com.yanan.frame.servlets.exception.MVCContextInitException;
import com.yanan.utils.ArrayUtils;
import com.yanan.utils.resource.scanner.PackageScanner;
import com.yanan.utils.resource.scanner.PackageScanner.ClassInter;

/**
 * 这是一个ServletBean构建器，用于将java bean构建成Servlet Bean的映射
 * <p>
 * 20200731 不在提供xml构建，重新修改配置的逻辑，全面采用Hoconf配置
 * 
 * @author yanan
 *
 */
public class ServletBuilder {
	// 单例模式
	private static volatile ServletBuilder servletInstance;
	// server映射
	private ServletMapping servletMannager;
	// 日志
	private final Logger logger = LoggerFactory.getLogger(ServletBuilder.class);
	private String[] packages;

	/**
	 * 获取ServletBuilder的扫描位置
	 * <p>
	 * 你只能拿到副本
	 * 
	 * @return 扫描位置
	 */
	public String[] getScanPath() {
		return Arrays.copyOf(packages, packages.length);
	}

	/**
	 * 初始化ServletBuilder
	 */
	private ServletBuilder() {
		this.servletMannager = ServletMapping.getInstance();
	}

	/**
	 * ServletBuilder为单例模式，此方法用于获取ServletBuilder实例
	 * 
	 * @return servletBuilder实例
	 */
	public static ServletBuilder getInstance() {
		// 使用环境保证单例
		Environment.getEnviroment().executeOnlyOnce(ServletBuilder.class, () -> {
			servletInstance = new ServletBuilder();
		});
		return servletInstance;
	}

	public void buildServletBean(String[] packages) {
		//将包添加到记录中
		this.packages = ArrayUtils.megere(this.packages,packages);
		//扫描路径
		PackageScanner scanner = new PackageScanner();
		scanner.addScanPath(packages);
		scanner.doScanner(new ClassInter() {
			@Override
			public void find(Class<?> cls) {
				buildServletBean(cls);
			}
		});
	}

	protected void buildServletBean(Class<?> cls) {
		try {
			logger.debug("scan class: " + cls.getName());
			Method[] methods = cls.getMethods();
			methodIterator: for (Method method : methods) {
				List<ServletDispatcher> sds = PlugsFactory.getPluginsInstanceList(ServletDispatcher.class);
				for (ServletDispatcher sd : sds) {
					Class<? extends Annotation>[] annosType = sd.getDispatcherAnnotation();
					for (Class<? extends Annotation> annoType : annosType) {
						if (annosType != null && method.getAnnotation(annoType) != null)
							if (sd.getBuilder().builder(annoType, method.getAnnotation(annoType), cls, method,
									servletMannager))
								continue methodIterator;
					}
				}
			}
		} catch (Exception e) {
			throw new MVCContextInitException("failed to build class " + cls, e);
		}
	}

	private String[] decodePackagesFromConfig(ServerContext serverContext) {
		ConfigValue configValue = serverContext.getConfig().getValue(Config_MVC_Constant.MVC_PACKAGES);
		if(configValue == null)
			return null;
		if (configValue.valueType() == ConfigValueType.STRING)
			return ((String) configValue.unwrapped()).split(",");
		if (configValue.valueType() == ConfigValueType.LIST) {
			ConfigList configList = (ConfigList) configValue;
			return configList.unwrapped().toArray(new String[configList.size()]);
		}
		throw new IllegalConfigException("the config " + Config_MVC_Constant.MVC_PACKAGES
				+ " only STRING or LIST,but found:" + configValue.valueType());
	}

	/**
	 * 初始化构造器
	 */
	public void init() {
		ServerContext serverContext = ServerContext.getContext();
		if (serverContext.getConfig() == null)
			return;
		String[] packages = decodePackagesFromConfig(serverContext);
		if(ArrayUtils.isEmpty(packages)) {
			logger.warn("mvc package config is null");
			return;
		}
		logger.info("mvc package config is " + Arrays.toString(packages));
		buildServletBean(packages);
	}
}