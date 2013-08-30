package com.openceres.core.executor;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;

import com.openceres.core.ActorManager;
import com.openceres.core.action.ActionTask;
import com.openceres.core.command.ResultCommand;
import com.openceres.core.command.TaskCommand;
import com.openceres.core.common.ActorRole;
import com.openceres.core.common.ActorStatus;
import com.openceres.core.common.ResultSet;

/**
 * 
 * <h1>TaskSingleJobActor</h1>
 * Ant Task 를 수행하는 actor
 *
 * @author changbaechoi
 *
 */
public class TaskSingleJobActor extends SingleJobActor
{	
	private static Logger LOG = LoggerFactory.getLogger(TaskSingleJobActor.class);
	
	public TaskSingleJobActor()
	{
		super(ActorRole.WORKER);
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof TaskCommand){
			preExecute((TaskCommand)message);
			Project project = new Project();
			project.init();
			
			Target target = new Target();
			TaskCommand command = (TaskCommand)message;
			ActionTask task  = (ActionTask)Class.forName(command.getTaskClassName()).newInstance();
			task.setCommand((TaskCommand)message);

			task.setProject(project);
			target.addTask(task);
			
			try {
				project.addTarget("exec", target);
				project.executeTarget("exec");
				
				project.fireBuildFinished(null);
			}
			catch (BuildException e)
			{
				e.printStackTrace();
				writeError(e.getMessage());
			}
			
			LOG.debug(getSelf().path().toString());
			ResultCommand resultCommand = new ResultCommand(command.getUid());
			ResultSet resultSet = ActorManager.getInstance().getCommandResults(command.getUid().toString());
			if(resultSet != null)
			{
				resultCommand.setSuccessFlag(resultSet.isSuccess);
				resultCommand.setResultMap(resultSet.resultMap);
			}
			else {
				resultSet = new ResultSet();
				resultCommand.setSuccessFlag(true);
				resultCommand.setResultMap(null);
			}
			getSender().tell(resultCommand, getSelf());

			postExecute((TaskCommand)message);
		} else if(message instanceof String && "PING".equals((String)message)) {
			LOG.debug("Get PING from " + getSender().path().toString());
			getSender().tell("PONG", getSelf());
		} else {
			unhandled(message);
		}

	}
	
	/**
	 * 메시지를 받을 때 호출하여 로그 메시지를 남긴다.
	 * @param command
	 */
	@Override
	public void preExecute(TaskCommand command) 
	{
		actorInfo.setStatus(ActorStatus.RUNNING);
		actorInfo.setStart(new Date());
		actorInfo.setCommand(command.getUid().toString());
		actorInfo.setDescription(command.getTaskClassName() + " Job Starting");
		
		writeLog();
		ActorManager.getInstance().setData(getSelf().path().toString(), actorInfo);
	}
	
	/**
	 * 메시지를 종료할 때 호출하여 로그 메시지를 남긴다.
	 * @param command
	 */
	@Override
	public void postExecute(TaskCommand command) 
	{
		actorInfo.setStatus(ActorStatus.COMPLETED);
		actorInfo.setStart(new Date());
		actorInfo.setCommand(command.getUid().toString());
		actorInfo.setDescription(command.getTaskClassName() + " Job Completed");
		
		writeLog();
		ActorManager.getInstance().setData(getSelf().path().toString(), actorInfo);
	}
}
