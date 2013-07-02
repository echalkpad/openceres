package com.openceres.model;

import java.util.Date;


public class TaskInfo extends BaseModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7710023787890531727L;
	
	String 	command;
	String 	policyId;
	String 	policyName;
	String	alarmId;
	String	alarmName;
	String 	scheduleId;
	String	scheduleName;
	String 	action;
	String 	user;
	Date 	timestamp;
	String	description;
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}


	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public String getAlarmName() {
		return alarmName;
	}

	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public String getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toJson()
	{
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("{");
		stringBuffer.append("\"command\":\"").append(command).append("\",");
		stringBuffer.append("\"policyId\":\"").append(policyId).append("\",");
		stringBuffer.append("\"policyName\":\"").append(policyName).append("\",");
		stringBuffer.append("\"alarmId\":\"").append(alarmId).append("\",");
		stringBuffer.append("\"alarmName\":\"").append(alarmName).append("\",");
		stringBuffer.append("\"scheduleId\":\"").append(scheduleId).append("\",");
		stringBuffer.append("\"scheduleName\":\"").append(scheduleName).append("\",");
		stringBuffer.append("\"action\":\"").append(action).append("\",");
		stringBuffer.append("\"user\":\"").append(user).append("\",");
		stringBuffer.append("\"timestamp\":").append(timestamp.getTime()).append(",");
		stringBuffer.append("\"description\":\"").append(description).append("\"");;
		stringBuffer.append("}");
		
		return stringBuffer.toString();
	}
}
