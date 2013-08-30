package com.openceres.core.action;

import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.openceres.core.ActorManager;
import com.openceres.core.command.TaskCommand;


/**
 * 
 * <h1>ActionTask</h1>
 * <h3>Task 명령을 수행하기 위한 추상 클래스.</h3>
 *
 * @author changbaechoi
 *
 */
public abstract class ActionTask extends Task {
		
	/**
	 * 태스크를 실행하는 명령 
	 */
	protected TaskCommand command = null;
	
	public ActionTask()
	{
		super();
	}
	
	public ActionTask(TaskCommand command)
	{
		super();
		this.setCommand(command);
	}
	
	public void setCommand(TaskCommand command)
	{
		this.command = command;
	}
	
	abstract protected boolean checkParam();
	
	abstract public void execute() throws BuildException;
	
	public void setResults(String commandId, boolean isSuccess, Map<String, Object>resultMap)
	{
		ActorManager actorManager = ActorManager.getInstance();
		
		actorManager.setCommandResults(commandId, isSuccess, resultMap);
	}

}
