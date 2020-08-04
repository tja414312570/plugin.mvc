package com.yanan.framework.webmvc.session;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yanan.framework.plugin.Plugin;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.builder.PluginDefinitionBuilderFactory;
import com.yanan.framework.plugin.interfacer.PlugsListener;
import com.yanan.framework.webmvc.session.entity.TokenEntity;
import com.yanan.framework.webmvc.session.interfaceSupport.TokenHibernateInterface;
import com.yanan.framework.webmvc.session.interfaceSupport.TokenListener;
import com.yanan.framework.webmvc.session.parameter.TokenParameterHandler;

@Register
public class TokenContextInit implements ServletContextListener,PlugsListener{
	private final Logger log = LoggerFactory.getLogger(TokenManager.class);
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if(TokenManager.isInstance())
			TokenManager.getInstance().destory();
	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		if(PlugsFactory.getPlugin(TokenHibernateInterface.class) == null) {
			Plugin plugin = PluginDefinitionBuilderFactory.builderPluginDefinition(TokenHibernateInterface.class);
			PlugsFactory.getInstance().addPlugininDefinition(plugin);
		}
			
		if(PlugsFactory.getPlugin(TokenListener.class) == null) {
			Plugin plugin = PluginDefinitionBuilderFactory.builderPluginDefinition(TokenListener.class);
			PlugsFactory.getInstance().addPlugininDefinition(plugin);
		}
		log.debug("================================================================================================================");
		log.debug("Start init Token plugin");
		log.debug("Get Token Data Hiberanate Interfacer:"+(PlugsFactory.getPlugin(TokenHibernateInterface.class).getDefaultRegisterDefinition()==null
				?null:PlugsFactory.getPlugin(TokenHibernateInterface.class).getDefaultRegisterDefinition().getRegisterClass().getName()));
		log.debug("Iterative Token Mapping");
		TokenManager.init();
		Plugin plugin = PluginDefinitionBuilderFactory.builderPluginDefinition(TokenParameterHandler.class);
		PlugsFactory.getInstance().addPlugininDefinition(plugin);
		Iterator<Entry<String, TokenEntity>> ei = TokenManager.getInstance().getTokenMap().entrySet().iterator();
		while(ei.hasNext()){
			Entry<String, TokenEntity> e = ei.next();
			log.debug("Map:"+e.getKey()+",Entity:"+e.getValue());	
			}
		}
	@Override
	public void execute(PlugsFactory plugsFactory) {
		Plugin plugin = PluginDefinitionBuilderFactory.builderPluginDefinition(TokenHibernateInterface.class);
		PlugsFactory.getInstance().addPlugininDefinition(plugin);
		plugin = PluginDefinitionBuilderFactory.builderPluginDefinition(TokenListener.class);
		PlugsFactory.getInstance().addPlugininDefinition(plugin);
	}
}