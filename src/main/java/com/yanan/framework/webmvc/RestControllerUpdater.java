package com.yanan.framework.webmvc;

import java.io.File;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.hot.ClassUpdateListener;
import com.yanan.framework.plugin.PlugsFactory;

@Register
public class RestControllerUpdater implements ClassUpdateListener{

	@Override
	public void updateClass(Class<?> originClass, Class<?> updateClass, Class<?> updateOrigin, File updateFile) {
		RestfulDispatcher restfulDispatcher = PlugsFactory.getPluginsInstance(RestfulDispatcher.class);
		restfulDispatcher.addServletByScanBean(updateClass);
		restfulDispatcher.rebuildServlet(originClass, updateClass);
	}

}