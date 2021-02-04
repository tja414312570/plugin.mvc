package com.yanan.framework.token;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map.Entry;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.yanan.framework.resource.ResourceLoaderException;
import com.yanan.framework.token.web.WebTokenManager;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.resource.Resource;
import com.yanan.utils.resource.ResourceManager;

public class TokenBuilderFactory {
	public static TokenManager builder(String path) {
		Resource resource = ResourceManager.getResource(path);
		Reader reader;
		try {
			reader = new InputStreamReader(resource.getInputStream());
		} catch (IOException e) {
			throw new ResourceLoaderException(e);
		}
		Config config = ConfigFactory.parseReader(reader);
		config.allowKeyNull();
		config = config.getConfig("token");
		return builderFromConfig(config);
	}

	private static TokenManager builderFromConfig(Config config) {
		config.allowKeyNull();
		TokenManager tokenManager = ReflectUtils.exists("com.yanan.framework.webmvc.RestfulDispatcher")
				?new WebTokenManager() : new TokenManager();
		int timeout = config.getInt("timeout", 300);
		config = config.getConfig("role");
		System.out.println(config);
		Iterator<Entry<String, ConfigValue>> iterator = config.entrySet().iterator();
		while(iterator.hasNext())
			System.out.println(iterator.next());
		tokenManager.setTimeout(timeout);
		return tokenManager;
	}
}
