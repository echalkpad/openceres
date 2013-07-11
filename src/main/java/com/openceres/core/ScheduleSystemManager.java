package com.openceres.core;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openceres.config.AsConfiguration;
import com.openceres.core.router.router.DefaultRouter;
import com.openceres.core.router.router.RestletRouter;
import com.openceres.core.scheduler.QuartzScheduler;
import com.openceres.property.Const;


public class ScheduleSystemManager implements SystemManager {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduleSystemManager.class);
	
	private CamelContext camelContext;
	private QuartzScheduler quartzSchedulerService;
	private static final ScheduleSystemManager scheduleSystemManager = new ScheduleSystemManager();
	
	private ScheduleSystemManager() {
	}
	
	public static ScheduleSystemManager getInstance() {
		return scheduleSystemManager;
	}
	
	@Override
	public void init() {
		camelContext = new DefaultCamelContext();
		camelContext.addComponent("jms", ActiveMQComponent.activeMQComponent(AsConfiguration.getValue(Const.GLOBAL_QUEUE_CONNECTION)));
		
		try {
			camelContext.addRoutes(new DefaultRouter());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeCamelException(e);
		}
		
		try {
			camelContext.addRoutes(new RestletRouter());
		} catch (Exception e) {
			e.printStackTrace();
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
		
		quartzSchedulerService = QuartzScheduler.getInstance();
		quartzSchedulerService.startScheduler();
	}

	@Override
	public void shutdown() {
		quartzSchedulerService.stopScheduler();
		
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
	
	public QuartzScheduler getQuartzSchedulerService() {
		return quartzSchedulerService;
	}
}
