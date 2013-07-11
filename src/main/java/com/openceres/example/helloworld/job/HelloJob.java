package com.openceres.example.helloworld.job;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.openceres.core.ScheduleSystemManager;
import com.openceres.core.command.TaskCommand;
import com.openceres.core.scheduler.job.BaseJob;

public class HelloJob implements BaseJob{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("Job Excecuted...");
		
		UUID uuid = UUID.randomUUID();
		
		// Create New Task
		TaskCommand command = new TaskCommand(uuid);
		//Set class name to execute the actual task
		command.setTaskClassName("com.openceres.example.action.HelloTask");
		//Define job type 
		command.setType(TaskCommand.SINGLE_TYPE_JOB);
		
		// Define parameter map for task 
		Map<String,Object> paramMap = new HashMap<String, Object>();
		paramMap.put("name", "cbjazz");
		command.setParemeterMap(paramMap);
		
		ScheduleSystemManager ssm = ScheduleSystemManager.getInstance();
		ssm.getCamelContext().createProducerTemplate()
				.sendBody("direct:command", command);
		
	}
}
