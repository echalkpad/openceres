package com.openceres.core.executor;

import com.openceres.core.common.ActorRole;

/**
 * 
 * <h1>SingleJobActor</h1>
 * 단일 작업을 수행하기 위한 actor
 *
 * @author changbaechoi
 *
 */
public abstract class SingleJobActor extends MonitorableJobActor{
	public SingleJobActor(ActorRole role)
	{
		super(role);
	}
}
