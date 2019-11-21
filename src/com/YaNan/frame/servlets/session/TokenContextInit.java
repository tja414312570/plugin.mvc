package com.YaNan.frame.servlets.session;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.interfacer.PlugsListener;
import com.YaNan.frame.servlets.session.entity.TokenEntity;
import com.YaNan.frame.servlets.session.interfaceSupport.TokenHibernateInterface;
import com.YaNan.frame.servlets.session.interfaceSupport.TokenListener;
import com.YaNan.frame.servlets.session.parameter.TokenParameterHandler;

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
		if(PlugsFactory.getPlug(TokenHibernateInterface.class) == null)
			PlugsFactory.getInstance().addPlugs(TokenHibernateInterface.class);
		if(PlugsFactory.getPlug(TokenListener.class) == null)
			PlugsFactory.getInstance().addPlugs(TokenListener.class);
		log.debug("================================================================================================================");
		log.debug("Start init Token plugin");
		log.debug("Get Token Data Hiberanate Interfacer:"+(PlugsFactory.getPlug(TokenHibernateInterface.class).getDefaultRegisterDescription()==null
				?null:PlugsFactory.getPlug(TokenHibernateInterface.class).getDefaultRegisterDescription().getRegisterClass().getName()));
		log.debug("Iterative Token Mapping");
		TokenManager.init();
		PlugsFactory.getInstance().addPlugs(TokenParameterHandler.class);
		Iterator<Entry<String, TokenEntity>> ei = TokenManager.getInstance().getTokenMap().entrySet().iterator();
		while(ei.hasNext()){
			Entry<String, TokenEntity> e = ei.next();
			log.debug("Map:"+e.getKey()+",Entity:"+e.getValue());	
			}
		}
	@Override
	public void execute(PlugsFactory plugsFactory) {
		plugsFactory.addPlugsByDefault(TokenHibernateInterface.class);
		plugsFactory.addPlugs(TokenListener.class);
	}
}
