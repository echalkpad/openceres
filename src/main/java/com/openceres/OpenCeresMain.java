package com.openceres;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openceres.core.ActorRemoteSystemManager;
import com.openceres.core.ActorSystemManager;
import com.openceres.core.ScheduleSystemManager;
import com.openceres.core.SystemManager;
import com.openceres.core.scheduler.QuartzScheduler;
import com.openceres.property.FrameworkConstant;

public class OpenCeresMain {
	private static final Logger LOG = LoggerFactory.getLogger(OpenCeresMain.class);

//	static {
//		PropertyConfigurator.configure("conf/log4j.properties");
//	}
  
	SystemManager sysManager = null;

	public void init(String serviceName, int clearFlag) {
		SystemManager sysManager = null;

		// Configuration setting
		FrameworkConstant.MODULE_NAME = serviceName;
		FrameworkConstant.setConfigure();

		switch (serviceName) {  
		case "scheduler":
			LOG.info("Starting ScheduledSystem...");
			sysManager = ScheduleSystemManager.getInstance();
			sysManager.startup();
			setScheduleSystem(clearFlag);
			break;
		case "akka":
			LOG.info("Starting AkkaSystem...");
			sysManager = ActorSystemManager.getInstance();
			sysManager.startup();
			break;
		case "remote":
			LOG.info("Starting Remote AkkaSystem...");
			sysManager = ActorRemoteSystemManager.getInstance();
			sysManager.startup();
			break;
//		case "notification":
//			LOG.info("Starting NotificationSystem...");
//			sysManager = NotificationSystemManager.getInstance();
//			sysManager.startup();
//			break;
		default:
			System.out
					.println("Command : java OpenCeresMain \"scheduler\"|\"akka\"");
			break;
		}

	}
	
	public void setScheduleSystem(int clearFlag) {
		try {
			QuartzScheduler scheduler = QuartzScheduler.getInstance();

			if (clearFlag == 1) // TODO Delete this code : Use clear flag for testing job....
			{
				scheduler.clear();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		OpenCeresMain autoScaleMain = new OpenCeresMain();
		if (args.length == 0) {
			autoScaleMain.init("scheduler", 0);
		} else if (args.length == 1) {
			autoScaleMain.init(args[0], 0);
		} else if (args.length == 2) {
			int clearFlag = Integer.parseInt(args[1]);
			autoScaleMain.init(args[0], clearFlag);
		}
	}
}
