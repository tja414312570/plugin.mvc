package com.yanan.framework.webmvc;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yanan.framework.webmvc.annotations.restful.ParameterType;
import com.yanan.framework.webmvc.exception.ServletExceptionHandler;
import com.yanan.framework.webmvc.exception.ServletRuntimeException;
import com.yanan.framework.webmvc.parameter.ParameterHandler;
import com.yanan.framework.webmvc.response.ResponseHandler;
import com.yanan.framework.webmvc.response.annotations.ResponseType;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.exception.RegisterNotFound;

/**
 * Restful 核心调配器
 * 
 * @version 1.0.0
 * @since jdk1.7
 * @enCoding UTF-8
 * @author yanan
 *
 */
public class RestfulDispatcher extends HttpServlet
		implements ServletDispatcher{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_METHOD_PARAM = "_method";
	private static final String ActionStyle = "RESTFUL_STYLE";
	private String contextType = "text/html;charset=utf-8";
	// 日志类，用于输出日志
	private final Logger log = LoggerFactory.getLogger( RestfulDispatcher.class);
	protected boolean showServerInfo = true;
	protected Servlet servlet;
	
	// 支持的注解类型
	private static final Class<?>[] annotations = { com.yanan.framework.webmvc.annotations.RequestMapping.class,
			com.yanan.framework.webmvc.annotations.GetMapping.class,
			com.yanan.framework.webmvc.annotations.PutMapping.class,
			com.yanan.framework.webmvc.annotations.PostMapping.class,
			com.yanan.framework.webmvc.annotations.DeleteMapping.class };
	private static final ServletMappingBuilder servletMappingBuilder = new ServletBeanBuilder();
	/**
	 * 此方法用于处理restful的整个业务逻辑，包括一下循序 1、获取真实的servletBean（url需要进行重组，否则不会识别 get post
	 * delete put等请求） 2、获取封装参数
	 * （从servletBean.getParameters()方法获取需要的参数，遍历每个参数，通过其注解或属性从PlugsFactory
	 * 获取对应参数处理器，获取参数,并封装，拦截器拦截的是ParameterHandler的getParameter方法，通过对其传入第一个参数
	 * 参数获取到servlet类中对应方法的参数，再获取参数的输入 Constraint.class的注解，对该注解输出结果进行验证并作出对 应响应）
	 * 3、调用方法（将第二步获取到的参数进行校验，没问题之后通过反射调用其方法） 4、处理响应
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 获得相对路径
		/**
		 * 这里应该组装Servlet 对servelt进行组装，判断请求方式 post情况下 包含 _method = delete put
		 * 则为对应的方法，否则为post @ 符号与数字组合表示url的类型
		 */
		// 获取ServletBean映射实例以及获取ServletBean
		ServletBean servletBean = getServletBean(request);
		// 判断ServletBean是否存在
		if (servletBean == null) {
			log.warn("servlet handle not found! url mapping :" + REQUEST_METHOD.getRequest(getUrlMapping(request)));
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		RequestContext.setCurrentRequestContext(servletBean, request, response);
		try {
			// 获取Servlet类代理对象的实例
			Object proxyObject = PlugsFactory.getPluginsInstance(servletBean.getServletClass());
			// 获取参数,参数验证通过拦截里面的方法完成
			List<Object> parameters = null;
			if (servletBean.getParameters() != null)
				try {
					parameters = this.urlencodedParameterBind(request, response, servletBean);
				} catch (ServletRuntimeException servletRuntimeException) {
					throw servletRuntimeException;
				} catch (Throwable e) {
					throw new ServletRuntimeException("failed to processing request paramter !\r\n at class : "
							+ servletBean.getServletClass().getName() + "\r\n at method : " + servletBean.getMethod(),
							e);
				}
			// 执行完之后，判断是否已将response提交，如果为提交，则判断返回结果
			if (!response.isCommitted()) {
				Object handlerResult;
				try {
					handlerResult = invokeProxyMethhod(request, response, servletBean, proxyObject, parameters);
				} catch (ServletRuntimeException servletRuntimeException) {
					throw servletRuntimeException;
				} catch (Throwable e) {
					throw new ServletRuntimeException(
							"failed to invoke servlet bean !\r\n at class : " + servletBean.getServletClass().getName()
									+ "\r\n at method : " + servletBean.getMethod() + "\r\n paramters : " + parameters,
							e);
				}
				// 执行完之后，判断是否已将response提交，如果为提交，则判断返回结果
				if (!response.isCommitted()) {
					// 判断返回结果是否为null,不为null则进行处理
					// 判断是否有ResponseType等注解
					List<Annotation> resultAnnotations = servletBean.getMethodAnnotation(ResponseType.class);
					if (resultAnnotations == null || resultAnnotations.isEmpty())
						resultAnnotations = servletBean.getClassAnnotation(ResponseType.class);
					// 若果没有获得ResponseType的注解，则根据返回结果寻找response handler
					if (resultAnnotations == null || resultAnnotations.isEmpty()) {
						if (handlerResult == null) {
							response.setStatus(200);
							response.getWriter().flush();
							response.getWriter().close();
						} else {
							Class<?> handlerResultType = handlerResult.getClass();
							try {
								ResponseHandler responseHandler = PlugsFactory.getPluginsInstanceByAttributeStrict(
										ResponseHandler.class, handlerResultType.getName());
								responseHandler.render(request, response, handlerResult, null, servletBean);
							}catch (RegisterNotFound e) {
								response.setContentType(contextType);
								response.getWriter().write(handlerResult.toString());
								response.getWriter().flush();
								response.getWriter().close();
							}
							
//							// 如果handler不为空，则调用handler，否则直接输出
//							if (responseHandler != null) {
//								// 通过responseHandler对结果进行渲染并输出
//								
//							} else {
//								
//							}
						}
					} else {
						// 获取第一个ResponseType的注解
						Annotation responseAnnotation = resultAnnotations.get(0);
						Class<?> annotationType = responseAnnotation.annotationType();
						ResponseHandler responseHandler = PlugsFactory
								.getPluginsInstanceByAttributeStrict(ResponseHandler.class, annotationType.getName());
						if (responseHandler == null)
							throw new ServletException(
									"could not found response handler for response type " + annotationType.getName());
						responseHandler.render(request, response, handlerResult, responseAnnotation, servletBean);
					}
				}
			}
			proxyObject = null;
			parameters = null;
		} catch (Throwable throwable) {
			doException(throwable, request, response, servletBean);
		}finally {
			RequestContext.remvoeCurrentContxt();
		}
	}
	/**
	 * 请求异常时处理
	 * @param throwable
	 * @param request
	 * @param response
	 * @param servletBean
	 * @throws IOException
	 * @throws ServletException
	 */
	private void doException(Throwable throwable,HttpServletRequest request,HttpServletResponse response,ServletBean servletBean) throws IOException, ServletException {
		//定义一个异常的处理链
		Set<ServletExceptionHandler> exceptionHandlerSet = new HashSet<ServletExceptionHandler>();
		Throwable t = throwable;
		while(t != null){
			List<ServletExceptionHandler> servletExceptionHandler = PlugsFactory
					.getPluginsInstanceListByAttribute(ServletExceptionHandler.class,t.getClass().getName());
			exceptionHandlerSet.addAll(servletExceptionHandler);
			t = t.getCause();
		}
		// 没有异常处理，默认错误处理方式
		if (exceptionHandlerSet.isEmpty()) {
			if (!response.isCommitted())
				if(throwable.getClass().equals(ServletRuntimeException.class)) {
					response.sendError(((ServletRuntimeException)throwable).getStatus(), throwable.getMessage());
				}else {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, throwable.getMessage());
				}
		}else {
			for(ServletExceptionHandler servletExceptionHandler : exceptionHandlerSet) {
				servletExceptionHandler.exception(throwable, request, response,servletBean);
			}
		}
	}

	/**
	 * Return the relative path associated with this servlet.
	 *
	 * @param request
	 *            The servlet request we are processing
	 * @return the relative path
	 */
	protected String getRelativePath(HttpServletRequest request) {
		return getRelativePath(request, false);
	}
	protected String getRelativePath(HttpServletRequest request, boolean allowEmptyPath) {
		String pathInfo;
		if (request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI) != null) {
			pathInfo = (String) request.getAttribute(RequestDispatcher.INCLUDE_PATH_INFO);
		} else {
			pathInfo = request.getPathInfo();
		}
		StringBuilder result = new StringBuilder();
		if (pathInfo != null) {
			result.append(pathInfo);
		}
		if (result.length() == 0 && !allowEmptyPath) {
			result.append('/');
		}
		return result.toString();
	}

	/**
	 * 此方法用于提取 get、post(content type:application/x-www-form-urlencoded)的数据
	 * 
	 * @param request
	 * @param response
	 * @param servletBean
	 * @param pathParameter
	 * @return
	 * @throws Exception
	 */
	protected List<Object> urlencodedParameterBind(HttpServletRequest request, HttpServletResponse response,
			ServletBean servletBean) throws Throwable {
		// 创建一个ParameterHandler的集合
		ParameterHandlerCache parameterHandlerCache = new ParameterHandlerCache(request, response, servletBean);
		// 获得servletBean中的参数 该集合类型为 参数 ==》注解类型 ==》注解
		Iterator<Entry<Parameter, Map<Class<Annotation>, List<Annotation>>>> paramIterator = servletBean.getParameters()
				.entrySet().iterator();
		// 如果存在参数 则新建一个servletBean方法的参数的集合
		List<Object> parameters = new LinkedList<Object>();
		// 获取请求参数的值得迭代器 封装调用参数
		while (paramIterator.hasNext()) {
			// 如果赋值过程强行提交了数据，则返回一个null
			if (response.isCommitted())
				return null;
			// 获取参数和其描述
			Entry<Parameter, Map<Class<Annotation>, List<Annotation>>> paramEntry = paramIterator.next();
			// 获取其中的para
			if (paramEntry.getValue() != null) {
				List<Annotation> annos = paramEntry.getValue().get(ParameterType.class);
				if (annos != null && annos.size() != 0) {
					Annotation parameterAnnotation = annos.get(0);
					// 通过参数注解获取对应的参数处理器
					ParameterHandler parameterHandler = parameterHandlerCache.getParameterHandler(parameterAnnotation);
					parameters.add(parameterHandler == null ? null
							: parameterHandler.getParameter(paramEntry.getKey(), parameterAnnotation));
					continue;
				}
			}
			ParameterHandler parameterHandler = parameterHandlerCache
					.getParameterHandler(paramEntry.getKey().getType());
			parameters.add(parameterHandler == null ? null : parameterHandler.getParameter(paramEntry.getKey()));
		}
		return parameters;
	}

	/**
	 * invoke proxy object servlet method
	 * 
	 * @param model
	 * @return
	 * @throws ServletException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws Exception
	 */
	public Object invokeProxyMethhod(HttpServletRequest request, HttpServletResponse response, ServletBean servletBean,
			Object proxyObject, List<Object> parameters)
			throws Throwable {
		Object[] parameter = null;
		// 判断需要的参数是否与获得的参数匹配
		if (servletBean.getParameters() != null) {
			if (parameters == null || servletBean.getParameters().size() != parameters.size()) {
				throw new ServletException(
						"url mapping " + this.getUrlMapping(request) + " method parameter not match");
			} else {// 重组准备参数 需要准备参数类型数组和参数数组
				parameter = new Object[parameters.size()];
				int i = 0;
				Iterator<Object> paraEn = parameters.iterator();
				while (paraEn.hasNext()) {
					parameter[i++] = paraEn.next();
				}
			}
		}
		Object result = servletBean.getMethod().invoke(proxyObject, parameter);
		return result;
	}

	@Override
	public ServletMappingBuilder getBuilder() {
		return servletMappingBuilder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> Class<T>[] getDispatcherAnnotation() {
		return (Class<T>[]) annotations;
	}

	@Override
	public ServletBean getServletBean(HttpServletRequest request) throws ServletException {
		return ServletMapping.getInstance().getServlet(ActionStyle, getUrlMapping(request));
	}

	public ServletBean getServletBean(String urlMapping) {
		return ServletMapping.getInstance().getServlet(ActionStyle, urlMapping);
	}

	public String getUrlMapping(HttpServletRequest request) throws ServletException {
		String urlMapping = getRelativePath(request);
		String method = request.getMethod();
		if (method.equals("GET")) {
			urlMapping = this.buildUrl(urlMapping, REQUEST_METHOD.GET);
		} else if (method.equals("POST")) {
			method = request.getParameter(DEFAULT_METHOD_PARAM);
			if (method == null) {
				urlMapping = this.buildUrl(urlMapping, REQUEST_METHOD.POST);
			} else if (method.toUpperCase().equals("DELETE")) {
				urlMapping = this.buildUrl(urlMapping, REQUEST_METHOD.DELETE);
			} else if (method.toUpperCase().equals("PUT")) {
				urlMapping = this.buildUrl(urlMapping, REQUEST_METHOD.PUT);
			} else if (method.toUpperCase().equals("POST")) {
				urlMapping = this.buildUrl(urlMapping, REQUEST_METHOD.POST);
			} else {
				throw new ServletException("servlet handle not process this request method ! request url :" + urlMapping
						+ ",method:" + method);
			}
			// 组装post delete put 请求
		} else if (method.equals("DELETE")) {
			urlMapping = this.buildUrl(urlMapping, REQUEST_METHOD.DELETE);
		} else if (method.equals("PUT")) {
			urlMapping = this.buildUrl(urlMapping, REQUEST_METHOD.PUT);
		} else if (method.equals("HEAD")) {
			urlMapping = this.buildUrl(urlMapping, REQUEST_METHOD.HEAD);
		} else if (method.equals("OPTIONS")) {
			urlMapping = this.buildUrl(urlMapping, REQUEST_METHOD.OPTIONS);
		} else if (method.equals("TRACE")) {
			urlMapping = this.buildUrl(urlMapping, REQUEST_METHOD.TRACE);
		} else {
			throw new ServletException("servlet handle not process this request method ! request url :" + urlMapping
					+ ",method:" + method);
		}
		return urlMapping;
	}

	private String buildUrl(String urlMapping, int type) {
		return new StringBuilder(urlMapping).append("@").append(type).toString();
	}

	/**
	 * 重构与类相关Servlet映射
	 * @param originClass
	 * @param updateClass
	 */
	public void rebuildServlet(Class<?> originClass, Class<?> updateClass) {
		Map<String, ServletBean> mapping = ServletMapping.getInstance().getServletMapping();
		Map<String, ServletBean> styleMapping = ServletMapping.getInstance().getServletMappingByStype(ActionStyle);
		Iterator<Entry<String, ServletBean>> iterator = mapping.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, ServletBean> entry = iterator.next();
			ServletBean bean = entry.getValue();
			if (bean.getServletClass().equals(originClass)) {
				log.debug("remove servlet mapping " + REQUEST_METHOD.getRequest(entry.getKey()) + ";info:"
						+ entry.getValue());
				styleMapping.remove(entry.getKey());
				iterator.remove();
			}else if(bean.getServletClass().equals(updateClass)){
				log.debug("add servlet mapping " + REQUEST_METHOD.getRequest(entry.getKey()) + ";info:"
						+ entry.getValue());
			}
		}
	}
	/**
	 * 添加servletBean通过扫描Bean
	 * @param updateClass
	 */
	public void addServletByScanBean(Class<?> updateClass) {
		Method[] methods = updateClass.getMethods();
		methodIterator: for (Method method : methods) {
			List<ServletDispatcher> sds = PlugsFactory.getPluginsInstanceList(ServletDispatcher.class);
			for (ServletDispatcher sd : sds) {
				Class<? extends Annotation>[] annosType = sd.getDispatcherAnnotation();
				for (Class<? extends Annotation> annoType : annosType) {
					if (annosType != null && method.getAnnotation(annoType) != null)
						if (sd.getBuilder().builder(annoType, method.getAnnotation(annoType), updateClass, method,
								ServletMapping.getInstance()))
							continue methodIterator;
				}
			}
		}
	}
}