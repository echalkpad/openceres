package com.openceres.core;

import org.apache.camel.CamelContext;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openceres.config.AsConfiguration;
import com.openceres.core.router.router.DefaultRouter;
import com.openceres.core.scheduler.QuartzScheduler;
import com.openceres.property.Const;
import com.openceres.util.ZkUtil;


public class ScheduleSystemManager implements SystemManager {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduleSystemManager.class);
	
	private ZkUtil zkUtil = null;
	private CamelContext camelContext;
	private QuartzScheduler quartzSchedulerService;
	private static final ScheduleSystemManager scheduleSystemManager = new ScheduleSystemManager();
	
	final String membership = "alarms";
	
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
		
		zkUtil = new ZkUtil(membership).withZkServers(AsConfiguration.getValue(Const.ZK_SERVERS));
		zkUtil.initialize();
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
		zkUtil.destroy();
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
	
	public ZkUtil getZkUtil() {
		return zkUtil;
	}
}
