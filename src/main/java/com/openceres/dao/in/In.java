package com.openceres.dao.in;

import java.util.List;

import com.openceres.model.TaskInfo;

public interface In {
	
	/**
	 * 태스크에 대한 정보를 조회한다. 
	 * 
	 * @param taskFilter	조회할 태스크의 정보
	 * @param startTime		검색 시작 시간 범위
	 * @param endTime		검색 종료 시간 범위
	 * @return				json 형태의 문자열 
	 */
	public String readTasksToJson(TaskInfo taskFilter, long startTime, long endTime);
	
	/**
	 * 태스크에 대한 정보를 조회한다. 
	 * 
	 * @param taskFilter	조회할 태스크의 정보
	 * @param startTime		검색 시작 시간 범위
	 * @param endTime		검색 종료 시간 범위
	 * @param page	
	 * @param limit
	 * @return				json 형태의 문자열 
	 */
	public String readTasksToJson(TaskInfo taskFilter, long startTime, long endTime, int page, int limit);
	
	/**
	 * 태스크에 대한 정보를 조회한다. 
	 * 
	 * @param taskFilter	조회할 태스크의 정보
	 * @param startTime		검색 시작 시간 범위
	 * @param endTime		검색 종료 시간 범위
	 * @return				태스크에 대한 배열  
	 */
	public List<TaskInfo> readTasks(TaskInfo taskFilter, long startTime, long endTime);
	
	/**
	 * 태스크에 대한 정보를 조회한다. 
	 * 
	 * @param taskFilter	조회할 태스크의 정보
	 * @param startTime		검색 시작 시간 범위
	 * @param endTime		검색 종료 시간 범위
	 * @param page	
	 * @param limit
	 * @return				태스크에 대한 배열  
	 */
	public List<TaskInfo> readTasks(TaskInfo taskFilter, long startTime, long endTime, int page, int limit);
	
	/**
	 * 태스크에 대한 정보를 조회한다. 
	 * 
	 * @param commandId		조회할 명령 정보 
	 * @return
	 */
	public List<TaskInfo> readTasksWithCommand(String commandId);
	
	/**
	 * For Web History
	 * 
	 * @param taskFilter
	 * @param startTime
	 * @param endTime
	 * @param skip
	 * @param limit
	 * @param filter
	 * @return
	 */
	public String readTaskForWeb(TaskInfo taskFilter, long startTime, long endTime, 
			int skip, int limit, int filter);
	
	/**
	 * For Task counter
	 * 
	 * @param taskFilter
	 * @param startTime
	 * @param endTime
	 * @param filter
	 * @return
	 */
	public long readTaskCount(TaskInfo taskFilter, long startTime, long endTime, int filter);
	
}
	