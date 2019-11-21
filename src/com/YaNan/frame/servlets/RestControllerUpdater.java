package com.YaNan.frame.servlets;

import java.io.File;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.hot.ClassUpdateListener;

@Register
public class RestControllerUpdater implements ClassUpdateListener{

	@Override
	public void updateClass(Class<?> originClass, Class<?> updateClass, Class<?> updateOrigin, File updateFile) {
		RestfulDispatcher restfulDispatcher = PlugsFactory.getPlugsInstance(RestfulDispatcher.class);
		restfulDispatcher.addServletByScanBean(updateClass);
		restfulDispatcher.rebuildServlet(originClass, updateClass);
	}

}
