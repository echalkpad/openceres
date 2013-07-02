package com.openceres.dao.in;

import java.util.List;

import com.openceres.model.ActorInfo;

public interface In {
	
	/**
	 * Raad the logs 
	 * 
	 * @param actorInfoFilter
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<ActorInfo> readLog(ActorInfo actorInfoFilter, long startTime, long endTime);
}	