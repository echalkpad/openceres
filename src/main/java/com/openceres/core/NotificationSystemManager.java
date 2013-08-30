package com.openceres.core;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openceres.config.AsConfiguration;
import com.openceres.core.router.router.NotificationRouter;
import com.openceres.property.Const;


public class NotificationSystemManager implements SystemManager {

	private static final Logger LOG = LoggerFactory.getLogger(NotificationSystemManager.class);
	
	private CamelContext camelContext;
	private static final NotificationSystemManager notificationSystemManager = new NotificationSystemManager();
	
	private NotificationSystemManager() {
	}
	
	public static NotificationSystemManager getInstance() {
		return notificationSystemManager;
	}
	
	@Override
	public void init() {
		camelContext = new DefaultCamelContext();
		camelContext.addComponent("jms", ActiveMQComponent.activeMQComponent(AsConfiguration.getValue(Const.GLOBAL_QUEUE_CONNECTION)));
		
		try {
			camelContext.addRoutes(new NotificationRouter());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeCamelException(e);
		}
	}

	@Override
	public void startup() {
		init();
		
		try {
			camelContext.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeCamelException(e);
		}
	}

	@Override
	public void shutdown() {
		try {
			camelContext.stop();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public CamelContext getCamelContext() {
		return camelContext;
	}
}
