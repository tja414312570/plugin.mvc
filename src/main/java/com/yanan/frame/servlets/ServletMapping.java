package com.yanan.frame.servlets;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.yanan.utils.string.PathMatcher;

/**
 * Servlet映射
 * @author yanan
 *
 */
public class ServletMapping {
	/**
	 * 排序规则
	 */
	private static Comparator<String> sort = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			int i1 = -1;
			int i2 = -1;
			int d = 0;
//			System.out.println("========      "+o1+"  "+o2);
			while(true){
				i1 = getMarkIndex(o1, i1+1);
				i2 = getMarkIndex(o2, i2+1);
				if(i1== -1 || i2 ==-1)
					break;
				d = i2-i1;
//				System.out.println(o1+"==>"+i1+"  "+o2+"==>"+ i2+"  =  "+d);
				if(d!=0)
					return d;
			}
			if(i1 == i2) {
				d =  o2.compareTo(o1);
			}else {
				d = i1 - i2;
			}
//			System.out.println(o1+"==>"+i1+"  "+o2+"==>"+ i2+"   =  "+d);
			return d;
		}
	};
	/**
	 * url ==>servletBean; 
	 * url such as *action.do  /action.do  /user/name/${id}
	 */
	private Map<String,ServletBean> servletMapping = new TreeMap<String,ServletBean>(sort);
	private Map<String,TreeMap<String, ServletBean>> typeServletMapping = new TreeMap<String,TreeMap<String, ServletBean>>(sort);
	//action存储：<namespace,<servletName,servletBean>>
//	private Map<String, Map<String, ServletBean>> servletMapping = new LinkedHashMap<String, Map<String, ServletBean>>();
	private static ServletMapping dsm;
	public static int getMarkIndex(String string, int offset) {
		int hI = string.indexOf("{",offset);
		int sI = string.indexOf("*",offset);
		int qI = string.indexOf("?",offset);
		int i = -1;
		if(hI!=-1)
			if(i != -1)
				i = Math.min(i, hI);
			else i = hI;
		if(sI!=-1)
			if(i != -1)
				i = Math.min(i, sI);
			else i = sI;
		if(qI!=-1)
			if(i != -1)
				i = Math.min(i, qI);
			else i = qI;
		return i;
	}
	public Map<String, ServletBean> getServletMapping() {
		return servletMapping;
	}
//	public static void main(String[] args) {
//		String str  = "/sldflsd/{lsdfs*}/k*ksdlfs?1";
//		String str2  = "/sldflsd/lsdfs/{**}k*ksdlfs?2";
//		String str3  = "/sldflsd/lsdfs/3";
//		String str4  = "/sldflsd/4{lsdfs*}/k*ksdlfs?4";
//		String str5  = "/api";
//		String str6  = "/api{xxx}";
////		System.out.println(str.compareTo(str2));
//		System.out.println(getMarkIndex(str, -1));
//		System.out.println(getMarkIndex(str2, -1));
//		System.out.println(getMarkIndex(str4, -1));
//		System.out.println(getMarkIndex(str, 0)-getMarkIndex(str2, 0));
//		Map<String,ServletBean> t = new TreeMap<String,ServletBean>(sort);
//		t.put(str, null);
//		t.put(str2, null);
//		t.put(str3, null);
//		t.put(str4, null);
//		t.put(str5, null);
//		t.put(str6, null);
//		System.out.println("     ---     ");
//		t.keySet().forEach(String -> {
//			System.out.println(String+"==>"+getMarkIndex(String, 0));
//		});
//	}
	/*
	 * 存储形式：
	 * 1，查看命名空间是否存在
	 * 2，存在则直接在命名空间存储
	 * 3，否者创建一个新的命名空间
	 */
	public synchronized void add(final ServletBean bean) {
		this.servletMapping.put(bean.getUrlmapping(), bean);
		TreeMap<String, ServletBean> servletBeanList = this.typeServletMapping.get(bean.getStyle());
		if(servletBeanList==null){
			servletBeanList = new TreeMap<String, ServletBean>(sort);
			servletBeanList.put(bean.getUrlmapping(),  bean);
			this.typeServletMapping.put(bean.getStyle(), servletBeanList);
		}else servletBeanList.put(bean.getUrlmapping(),  bean);
	}

	public void setServletMapping(Map<String, ServletBean> servletMapping) {
		this.servletMapping = servletMapping;
	}
	
	/**
	 * 精确的查找ServletBean
	 * @param url
	 * @return
	 */
	public ServletBean getServlet(String url) {
		Iterator<Entry<String, ServletBean>> iterator = this.servletMapping.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, ServletBean> entry = iterator.next();
			if(PathMatcher.match(entry.getKey(), url).isMatch())
				return entry.getValue();
		}
		return null;
	}
	/**
	 * 模糊查找ServletBean
	 * @param url
	 * @return
	 */
	public ServletBean getAsServlet(String url) {
		Iterator<Entry<String, ServletBean>> iterator = this.servletMapping.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, ServletBean> entry = iterator.next();
			String urlMapping = entry.getKey();
			if(urlMapping.indexOf("@")>=0)
				urlMapping=urlMapping.substring(0, urlMapping.length()-2);
			if(PathMatcher.match(urlMapping, url).isMatch())
				return entry.getValue();
		}
		return null;
	}
	public boolean includeServlet(String url) {
		return getServlet(url)!=null;
	}
	public boolean asIncludeServlet(String url) {
		return getAsServlet(url)!=null;
	}
	private ServletMapping() {
	}

	public static ServletMapping getInstance() {
		dsm = (dsm == null ? new ServletMapping() : dsm);
		return dsm;
	}
	public Iterator<String> getActionKSIterator() {
		return this.servletMapping.keySet().iterator();
	}
	public Map<String, ServletBean> getServletMappingByStype(String type) {
		return this.typeServletMapping.get(type);
	}
	public ServletBean getServlet(String actionStyle, String url) {
		Map<String, ServletBean> map = this.typeServletMapping.get(actionStyle);
		if(map==null)
			return null;
		Iterator<Entry<String, ServletBean>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, ServletBean> entry = iterator.next();
			if(PathMatcher.match(entry.getKey(), url).isMatch())
				return entry.getValue();
		}
		return null;
	}
}