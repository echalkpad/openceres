package com.openceres.core.command;

import java.util.Map;
import java.util.UUID;


/**
 * 
 * <h1>ActionCommand</h1>
 *
 * Action Module 을 위한 기본 명령 
 *
 * @author changbaechoi
 *
 */
public class TaskCommand extends TimerBaseCommand {
	/**
	 * 
	 */
	private static final long serialVersionUID = 271699790864797033L;
	
	static public int SINGLE_TYPE_JOB = 0;
	static public int TRANSACTION_TYPE_JOB = 1;
	
	//0 : single Job, 1 : sequential Job
	int type = 0;
	
	String taskClassName = "";
	
	TaskCommand parent = null;
	TaskCommand child = null;
	
	Map<String, Object> paremeterMap;
	
	public TaskCommand(UUID uid)
	{
		super(uid);
	}
	
	public TaskCommand(UUID uid, String taskClassName)
	{
		super(uid, 0);
		this.taskClassName = taskClassName;
	}
	
	public TaskCommand(UUID uid, int timeout, String taskClassName)
	{
		super(uid, timeout);
		this.taskClassName = taskClassName;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public String getTaskClassName() {
		return taskClassName;
	}

	public void setTaskClassName(String taskClassName) {
		this.taskClassName = taskClassName;
	}

	public void setParent(TaskCommand pCommand)
	{
		this.parent = pCommand;
	}
	
	public TaskCommand getParent()
	{
		return parent;
	}
	
	public void setChild(TaskCommand cCommand)
	{
		this.child = cCommand;
	}
	
	public TaskCommand getChild()
	{
		return child;
	}

	public Map<String, Object> getParemeterMap() {
		return paremeterMap;
	}

	public void setParemeterMap(Map<String, Object> paremeterMap) {
		this.paremeterMap = paremeterMap;
	}
	
	public void setResult(boolean isSuccess, Map<String, Object> resultMap) {
		
		
	}
}
