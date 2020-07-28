package com.yanan.frame.servlets;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import com.yanan.frame.plugin.Environment;
import com.yanan.frame.plugin.PlugsFactory;
import com.yanan.utils.resource.scanner.PackageScanner;
import com.yanan.utils.resource.scanner.PackageScanner.ClassInter;
import com.yanan.utils.web.WebPath;

/**
 * The ServletBuilder be used read servlet file or servlet configure
 * <p>
 * scan class file and manager servlet mapping
 * 
 * @author Administrator
 *
 */
public class ServletBuilder {
	private static volatile ServletBuilder servletInstance;
	private ServletMapping servletMannager;
	private List<Document> servletPaths = new ArrayList<Document>();
	private final Logger log = LoggerFactory.getLogger(ServletBuilder.class);
	private String[] packageDirs;

	/**
	 * get plugin context package scan paths
	 * 
	 * @return package path
	 */
	public String[] getScanPath() {
		return Arrays.copyOf(packageDirs, packageDirs.length);
	}

	private ServletBuilder() {
		if (WebPath.getWebPath() != null && WebPath.getWebPath().getClassPath() != null) {
			String servletCfg = WebPath.getWebPath().getClassPath().realPath + "servlet.xml";
			if (new File(servletCfg).exists())
				addServletXml(servletCfg);
		}
		this.servletMannager = ServletMapping.getInstance();
		this.init();
	}

	/**
	 * get ServletBuilder instance
	 * 
	 * @return
	 */
	public static ServletBuilder getInstance() {
		if (servletInstance == null) {
			synchronized (ServletBuilder.class) {
				if (servletInstance == null) {
					servletInstance = new ServletBuilder();
				}
			}
		}
		return servletInstance;
	}

	/**
	 * 添加servlet文件路径
	 * 
	 * @param xmlPath
	 */
	@SuppressWarnings("unchecked")
	public void addServletXml(String xmlPath) {
		if (new File(xmlPath).exists()) {
			try {
				Document doc = toDocument(xmlPath);
				Node root = doc.getRootElement();
				List<Element> includeNode = root.selectNodes("include");
				for (Element e : includeNode) {
					String namespace = e.attributeValue("namespace");
					namespace = (namespace.equals("/") ? "" : namespace);
					String file = e.getTextTrim();
					String Path = WebPath.getWebPath().getClassPath().realPath + namespace + file;
					if (new File(Path).exists()) {
						addServletXml(Path);
					} else {
						log.error("could not find servlet defalut configure file '" + Path
								+ "',mabye some system function is not work at this framework");
					}
				}
				servletPaths.add(doc);
			} catch (DocumentException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			}
		} else {
			log.error("could not find servlet xml file at xml path : " + xmlPath);
		}
	}

	private Document toDocument(String xmlPath) throws DocumentException {
		SAXReader reader = new SAXReader();
		return reader.read(xmlPath);
	}

	/**
	 * initial servlet mapping by scan
	 */
	public void initByScanner() {
		// try get Configure
		tryGetScanPackage();
		this.initByScanner(this.packageDirs);
	}

	private void initByScanner(String... paths) {
			PackageScanner scanner = new PackageScanner();
			scanner.addScanPath(paths);
			scanner.doScanner(new ClassInter() {
				@Override
				public void find(Class<?> cls) {
					try {
						log.debug("scan class: "+cls.getName());
						Method[] methods = cls.getMethods();
						methodIterator: for (Method method : methods) {
							List<ServletDispatcher> sds = PlugsFactory.getPluginsInstanceList(ServletDispatcher.class);
							for (ServletDispatcher sd : sds) {
								Class<? extends Annotation>[] annosType = sd.getDispatcherAnnotation();
								for (Class<? extends Annotation> annoType : annosType) {
									if (annosType != null && method.getAnnotation(annoType) != null)
										if (sd.getBuilder().builder(annoType, method.getAnnotation(annoType), cls,
												method, servletMannager))
											continue methodIterator;
								}
							}
						}
					} catch (Exception e) {
						log.error("An error occurs when scanning class " + cls.getName(), e);
					}

				}
			});
		}
	/**
	 * try get configure file
	 */
	private void tryGetScanPackage() {
		//先从config获取扫描地址
		ConfigValue configValue = Environment.getEnviroment().getConfigValue(Constant.MVC_CONFIG_SCANNER_TOKEN);
		//没有的话从环境变量获取地址
		if (configValue != null) {
			packageDirs = tryGetScanFromConfig(configValue);
		} else {
			String packages = Environment.getEnviroment().getVariable(Constant.MVC_SCANNER);
			if(packages != null) {
				packageDirs = packages.split(",");
			}
		}
		log.debug("Servlets scan package is "+Arrays.toString(packageDirs));
	}

	private String[] tryGetScanFromConfig(ConfigValue configValue) {
		if(configValue.valueType() == ConfigValueType.STRING) 
			return ((String)configValue.unwrapped()).split(",");
		if(configValue.valueType() == ConfigValueType.LIST) {
			ConfigList configList = (ConfigList) configValue;
			return configList.toArray(new String[configList.size()]);
		}
		throw new IllegalConfigException("the config "+Constant.MVC_CONFIG_SCANNER_TOKEN+" only STRING or LIST,but found:"+configValue.valueType());
	}

	public void init() {
		this.initByScanner();
	}
}