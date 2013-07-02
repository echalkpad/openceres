package com.openceres.core;

import java.util.Date;

public interface IScheduler {
	
	/**
	 * (반복 작업) 스케쥴을 추가한다.
	 * 
	 * @param groupID
	 * @param jobID
	 * @param triggerGroup
	 * @param triggerID
	 * @param cronExpression
	 * @param startTime
	 * @param endTime
	 * @param repeat
	 * @param jobClass
	 * @return
	 */
	public boolean addSchedule(String groupID, String jobID, String triggerGroup, String triggerID, 
			String cronExpression, Date startTime, Date endTime, int repeat, Class jobClass);
	
	/**
	 * (반복 작업) 스케쥴을 추가한다.
	 * 
	 * @param groupID
	 * @param jobID
	 * @param triggerGroup
	 * @param triggerID
	 * @param inteval (Second)
	 * @param startTime
	 * @param endTime
	 * @param repeat
	 * @param jobClass
	 * @return
	 */
	public boolean addSchedule(String groupID, String jobID, String triggerGroup, String triggerID, 
			int interval, Date startTime, Date endTime, int repeat, Class jobClass);
	
	/**
	 * (일회성) 스케쥴을 추가한다.
	 * 
	 * @param groupId
	 * @param jobId
	 * @param triggerGroup
	 * @param triggerId
	 * @param executeTime 스케쥴을 실행할 시간 (YYYY-MM-DD HH:mm:SS)
	 * @param jobClass
	 * @return
	 */
	public boolean addScheduleOnce(String groupId, String jobId, String triggerGroup, String triggerId, 
			Date executeTime, Class jobClass);
	
	/**
	 * (크론 작업) 스케쥴 정보를 업데이트 한다. 
	 * 
	 * @param groupId
	 * @param jobId
	 * @param cronExpression
	 * @param startTime
	 * @param endTime
	 * @param repeat
	 */
	public void updateSchedule(String groupId, String jobId, 
			String cronExpression, Date startTime, Date endTime, int repeat);

	/**
	 * (반복 작업) 스케쥴 정보를 업데이트 한다. 
	 * 
	 * @param groupId
	 * @param jobId
	 * @param interval
	 * @param startTime
	 * @param endTime
	 * @param repeat
	 */
	public void updateSchedule(String groupId, String jobId, 
			int interval, Date startTime, Date endTime, int repeat);
	
	/**
	 * (일회성) 스케쥴 정보를 업데이트 한다. 
	 * 
	 * @param groupId
	 * @param jobId
	 * @param executeTime
	 */
	public void updateSchedule(String groupId, String jobId, 
			Date executeTime);
	
	/**
	 * 스케쥴 정보를 삭제한다. 
	 * 
	 * @param groupId
	 * @param jobId
	 * @return
	 */
	public boolean deleteSchedule(String groupId, String jobId);
	
	/**
	 * 스케쥴을 시작한다. 
	 * 
	 * @return
	 */
	public boolean startScheduler();
	
	/**
	 * 스케쥴을 종료한다. 
	 * 
	 * @return
	 */
	public boolean stopScheduler();
}
