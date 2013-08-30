package com.openceres.model;

import java.util.Date;

public class ScheduleInfo {
	String groupId;
	String jobId;
	
	Date startTime;
	Date endTime;
	Date prevExeTime;
	Date nextExeTime;

	int misFiredInstruction;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getPrevExeTime() {
		return prevExeTime;
	}

	public void setPrevExeTime(Date prevExeTime) {
		this.prevExeTime = prevExeTime;
	}

	public Date getNextExeTime() {
		return nextExeTime;
	}

	public void setNextExeTime(Date nextExeTime) {
		this.nextExeTime = nextExeTime;
	}

	public int getMisFiredInstruction() {
		return misFiredInstruction;
	}

	public void setMisFiredInstruction(int misFiredInstruction) {
		this.misFiredInstruction = misFiredInstruction;
	}
}
