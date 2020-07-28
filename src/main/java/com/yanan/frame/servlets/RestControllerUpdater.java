package com.yanan.frame.servlets;

import java.io.File;

import com.yanan.frame.plugin.annotations.Register;
import com.yanan.frame.plugin.hot.ClassUpdateListener;
import com.yanan.frame.plugin.PlugsFactory;

@Register
public class RestControllerUpdater implements ClassUpdateListener{

	@Override
	public void updateClass(Class<?> originClass, Class<?> updateClass, Class<?> updateOrigin, File updateFile) {
		RestfulDispatcher restfulDispatcher = PlugsFactory.getPluginsInstance(RestfulDispatcher.class);
		restfulDispatcher.addServletByScanBean(updateClass);
		restfulDispatcher.rebuildServlet(originClass, updateClass);
	}

}