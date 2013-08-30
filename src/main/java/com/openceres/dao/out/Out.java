package com.openceres.dao.out;

import com.openceres.model.ActorInfo;

public interface Out {

	/**
	 * Write the actor's execution status. 
	 * 
	 * @param actorInfo	actor information
	 */
	public void writeLog(ActorInfo actorInfo);
	

}
