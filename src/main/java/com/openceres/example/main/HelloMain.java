package com.openceres.example.main;

import java.util.Date;

import com.openceres.core.IScheduler;
import com.openceres.core.ScheduleSystemManager;
import com.openceres.core.SystemManager;
import com.openceres.core.scheduler.QuartzScheduler;
import com.openceres.property.FrameworkConstant;

public class HelloMain {
	public static void main(String args[]) throws Exception{
		FrameworkConstant.setConfigure();
		
		SystemManager sysManager = ScheduleSystemManager.getInstance();
		sysManager.startup();
		
		IScheduler scheduler = QuartzScheduler.getInstance();
		scheduler.addSchedule("Example", "Hello", "Example", "Hello", 60, new Date(), null, 10, com.openceres.example.job.HelloJob.class);
		System.out.println("HERE!!!");
		int i = 0;
		while(true) {
			Thread.sleep(60 * 1000);
			if(i++ == 10) break;
		}
	}
}
