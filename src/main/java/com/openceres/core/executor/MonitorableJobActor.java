package com.openceres.core.executor;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.curator.framework.recipes.cache.PathChildrenCache;
import com.openceres.config.AsConfiguration;
import com.openceres.core.ActorManager;
import com.openceres.core.command.TaskCommand;
import com.openceres.core.common.ActorRole;
import com.openceres.core.common.ActorStatus;
import com.openceres.core.executor.listener.AkkaNodeListener;
import com.openceres.dao.OutFactory;
import com.openceres.dao.out.Out;
import com.openceres.model.ActorInfo;
import com.openceres.property.Const;

/**
 * 
 * <h1>MonitorableJobActor</h1>
 * 
 * Zookeeper를 통해 현재 상태를 전달할 수 있는 actor
 *
 * @author changbaechoi
 *
 */
public abstract class MonitorableJobActor extends BaseJobActor
{
	private static final Logger LOG = LoggerFactory.getLogger(MonitorableJobActor.class);
	
	/**
	 * Actor 에 대한 정보
	 */
	ActorInfo 	actorInfo = new ActorInfo();
	
	PathChildrenCache pathChildrenCache = null;
	AkkaNodeListener akkaNodeListener = null;
	
	Out logOuter = null;
	
	public MonitorableJobActor()
	{
		super();
		actorInfo.setRole(ActorRole.NONE);
		actorInfo.setUri(this.getSelf().path().toString());
		logOuter = OutFactory.getInstance(AsConfiguration.getValue(Const.LOGGER_DB));
	}
	
	public MonitorableJobActor(ActorRole role)
	{
		super();
		actorInfo.setRole(role);
		actorInfo.setUri(this.getSelf().path().toString());
	}
	
	public MonitorableJobActor(ActorRole role, String uri)
	{
		super();
		actorInfo.setRole(role);
		actorInfo.setUri(uri);
	}
	
	@Override
	public void postStop() {
		ActorManager.getInstance().removeActor(actorInfo.getUri(), actorInfo);
		LOG.info(actorInfo.getUri() + ": stopped!!!");
	}

	@Override
	public void preStart() {
		actorInfo.setStatus(ActorStatus.AVAILABLE);
		actorInfo.setStart(new Date());
		
		ActorManager.getInstance().addActor(actorInfo.getUri(), actorInfo);
		LOG.info(actorInfo.getUri() + ": started!!!");
	}
	
	public List<ActorInfo> getCachedNodes()
	{
		try {
			return ActorManager.getCachedInfo(pathChildrenCache);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * 메시지를 받을 때 호출하여 로그 메시지를 남긴다.
	 * @param command
	 */
	public abstract void preExecute(TaskCommand command);

	/**
	 * 메시지를 종료할 때 호출하여 로그 메시지를 남긴다.
	 * @param command
	 */
	public abstract void postExecute(TaskCommand command);
	
	public void timeoutExecute(TaskCommand command) {
		actorInfo.setStatus(ActorStatus.TIMEOUT);
		actorInfo.setStart(new Date());
		actorInfo.setCommand(command.getUid().toString());
		actorInfo.setDescription("Message becomes timeout");
		
		writeLog();
	}
	
	public void writeLog()
	{
		logOuter.writeLog(actorInfo);
	}
	
	public void writeLog(String message)
	{
		actorInfo.setDescription(message);
		logOuter.writeLog(actorInfo);
	}
	
	public void writeError(String message)
	{
		actorInfo.setDescription("ERROR:" + message);
		logOuter.writeLog(actorInfo);
	}
	
	public ActorInfo getActorInfo()
	{
		return actorInfo;
	}
}
