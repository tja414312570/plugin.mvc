package com.yanan.frame.servlets;

import javax.servlet.ServletContext;

/**
 * support web server context
 * @author yanan
 *
 */
public class ServerContext {
	private static volatile ServerContext serverContext;
	public static final String GLASSFISH_ID = "glassfish";

	public static final String JBOSS_ID = "jboss";

	public static final String JETTY_ID = "jetty";

	public static final String JONAS_ID = "jonas";

	public static final String OC4J_ID = "oc4j";

	public static final String RESIN_ID = "resin";

	public static final String TOMCAT_ID = "tomcat";

	public static final String WEBLOGIC_ID = "weblogic";

	public static final String WEBSPHERE_ID = "websphere";

	public static final String WILDFLY_ID = "wildfly";
	private final ServerType serverType;
	private ServletContext servletContext;
	private ServerContext() {
		serverType = tryGetServerType();
	}
	/**
	 * get server context
	 * @return
	 */
	public static ServerContext getContext() {
		if(serverContext == null) {
			synchronized (ServerContext.class) {
				if(serverContext == null) {
					serverContext = new ServerContext();
					
				}
			}
		}
		return serverContext;
	}
	
	public boolean isGlassfish() {
		if (serverType == ServerType.GLASSFISH) {
			return true;
		}

		return false;
	}

	public boolean isJBoss() {
		if (serverType == ServerType.JBOSS) {
			return true;
		}

		return false;
	}
	public boolean isJetty() {
		if (serverType == ServerType.JETTY) {
			return true;
		}

		return false;
	}

	public boolean isJOnAS() {
		if (serverType == ServerType.JONAS) {
			return true;
		}

		return false;
	}

	public boolean isOC4J() {
		if (serverType == ServerType.OC4J) {
			return true;
		}

		return false;
	}

	public boolean isResin() {
		if (serverType == ServerType.RESIN) {
			return true;
		}

		return false;
	}

	public static boolean isSupported(String serverType) {
		if (serverType.equals(ServerContext.TOMCAT_ID) ||
			serverType.equals(ServerContext.WEBLOGIC_ID) ||
			serverType.equals(ServerContext.WEBSPHERE_ID) ||
			serverType.equals(ServerContext.WILDFLY_ID)) {

			return true;
		}

		return false;
	}

	public static boolean isSupportsHotDeploy() {
		return true;
	}

	public boolean isTomcat() {
		return serverType == ServerType.TOMCAT;
	}

	public boolean isWebLogic() {
		return serverType == ServerType.WEBLOGIC;
	}

	public boolean isWebSphere() {
		return serverType == ServerType.WEBSPHERE;
	}

	public boolean isWildfly() {
		return serverType == ServerType.WILDFLY;
	}

	private static boolean _detect(String className) {
		try {
			ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
			systemClassLoader.loadClass(className);
			return true;
		}
		catch (ClassNotFoundException cnfe) {
			if (ServerContext.class.getResource(className) != null) {
				return true;
			}
			return false;
		}
	}

	public static ServerType tryGetServerType() {
		if (_hasSystemProperty("com.sun.aas.instanceRoot")) {
			return ServerType.GLASSFISH;
		}

		if (_hasSystemProperty("jboss.home.dir")) {
			return ServerType.JBOSS;
		}

		if (_hasSystemProperty("jonas.base")) {
			return ServerType.JONAS;
		}

		if (_detect("oracle.oc4j.util.ClassUtils")) {
			return ServerType.OC4J;
		}

		if (_hasSystemProperty("resin.home")) {
			return ServerType.RESIN;
		}

		if (_detect("/weblogic/Server.class")) {
			return ServerType.WEBLOGIC;
		}

		if (_detect("/com/ibm/websphere/product/VersionInfo.class")) {
			return ServerType.WEBSPHERE;
		}

		if (_hasSystemProperty("jboss.home.dir")) {
			return ServerType.WILDFLY;
		}

		if (_hasSystemProperty("jetty.home")) {
			return ServerType.JETTY;
		}

		if (_hasSystemProperty("catalina.base")) {
			return ServerType.TOMCAT;
		}

		return ServerType.UNKNOWN;
	}

	private static boolean _hasSystemProperty(String key) {
		String value = System.getProperty(key);
		return value != null;
	}

	public ServerType servetType() {
		return serverType;
	}
	public static enum ServerType {
		GLASSFISH, JBOSS, JETTY, JONAS, OC4J, RESIN, TOMCAT, UNKNOWN, WEBLOGIC,
		WEBSPHERE, WILDFLY
	}
	public void init(ServletContext servletContext) {
		this.setServletContext(servletContext);
	}
	public ServerType getServerType() {
		return serverType;
	}
	public ServletContext getServletContext() {
		return servletContext;
	}
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}