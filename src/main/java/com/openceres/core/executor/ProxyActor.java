package com.openceres.core.executor;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Await;
import scala.concurrent.Future;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheEvent;
import com.netflix.curator.utils.ZKPaths;
import com.openceres.config.AsConfiguration;
import com.openceres.core.ActorManager;
import com.openceres.core.command.TaskCommand;
import com.openceres.core.common.ActorRole;
import com.openceres.core.common.ActorStatus;
import com.openceres.core.executor.listener.AkkaNodeListener;
import com.openceres.model.ActorInfo;
import com.openceres.property.Const;

public class ProxyActor extends MonitorableJobActor {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProxyActor.class);
	
	private Map<String, ActorRef> actorMap = new HashMap<String, ActorRef>();
	
	Timeout timeout;
	
	public class MasterActorListener extends AkkaNodeListener
	{
		@Override
		public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
				throws Exception {
			
			String zooPath = ZKPaths.getNodeFromPath(event.getData().getPath());
			StringBuffer replacePath = new StringBuffer();
			for(int i = 0 ; i < zooPath.length() ; i++)
			{
				if(zooPath.charAt(i) == '*')
					replacePath.append("/");
				else
					replacePath.append(zooPath.charAt(i));
			}
			
			switch ( event.getType() )
            {
                case CHILD_ADDED:
                {
                	LOG.info("Node added: " +  replacePath.toString() );
                	
                    addActor(replacePath.toString()); 
                    break;
                }


                case CHILD_UPDATED:
                {
                	LOG.info("Node changed: " + replacePath.toString());
                    break;
                }


                case CHILD_REMOVED:
                {
                    LOG.info("Node removed: " + replacePath.toString());
                    removeActor(replacePath.toString()); 
                    break;
                }
				default:
				{
					LOG.warn("Unmatched event : " + event.getType().name());
					break;
				}
            }
		}
	}
	
	public ProxyActor() {
		super(ActorRole.PROXY);
		
		init();	
	}
	
	@SuppressWarnings("serial")
	private void init()
	{
		timeout = new Timeout(5, TimeUnit.SECONDS);
		// 현재 zookeeper 에 있는 마스터 액터 리스트를 가져온다.
		List<ActorInfo> actorInfoList = ActorManager.getInstance()
				.getDataByRole(ActorRole.MASTER);
		
		// 리모트에 액터가 존재하지 않으면 로컬 master actor로 시작한다.
		if (actorInfoList == null || actorInfoList.size() == 0) {
			LOG.warn("There is not remote actor... We will create a local actor");
			final int nrofworkers = AsConfiguration.getIntValue(Const.AKKA_WORKER_COUNT, 5);
			ActorRef actorRef = getContext().actorOf(
					new Props(new UntypedActorFactory() {
						public UntypedActor create() {
							return new MasterActor(nrofworkers);
						}
					}), "local");
			actorMap.put(actorRef.path().toString(), actorRef);
		} else {
			for (ActorInfo actorInfo : actorInfoList) {
				LOG.info("Master Actor Detected..." + actorInfo.getUri());

				addActor(actorInfo.getUri());
			}
		}

		// Master Actor에 대하여 모니터링을 시작한다. 
		try {
			pathChildrenCache = ActorManager.getInstance()
					.createCacheWithListener(ActorRole.MASTER,
							new MasterActorListener());
		} catch (Exception e) {
			LOG.error("Actor Listener add failed...: ",  e);
			System.exit(0);
		}
	}
		
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof TaskCommand) {
			TaskCommand taskCommand = (TaskCommand) message;
			preExecute(taskCommand);
			
			int timeout = taskCommand.getTimeout();
			if(timeout != 0) {
				long starttime = taskCommand.getStartTime();
				int checktime = (int) (System.currentTimeMillis() - starttime);
				if(checktime > timeout) {
					LOG.warn(taskCommand.getUid().toString() + " message is timeout [" + checktime + ">" + timeout + "]");
					timeoutExecute(taskCommand);
					return;
				}
			}
			
			while (true) {
				ActorRef actorRef = getAvailableActor();
				
				if(actorRef == null)
				{
					LOG.warn("There is not available actor. Sleeping...");
					Thread.sleep(1000);
					continue;
				}
			
				actorRef.forward(message, getContext());
				break;
			}
			postExecute(taskCommand);
		} else {
			unhandled(message);
		}
	}
	
	private ActorRef getAvailableActor()
	{
		Random rand = new Random();
		try {
			List<ActorInfo> actorInfoList = ActorManager.getCachedInfo(pathChildrenCache);
			ActorRef actorRef = null;
			
			int randNum = rand.nextInt(actorInfoList.size());
			int index = randNum;
			for(int i = 0 ; i < actorInfoList.size(); i++)
			{
				if(randNum + i >= actorInfoList.size())
				{
					index = (randNum  + i) - actorInfoList.size(); 
				}
				else
				{
					index = randNum + i;
				}
				
				ActorInfo actorInfo = actorInfoList.get(index);
				
				if(actorMap.containsKey(actorInfo.getUri()))
				{
					actorRef = actorMap.get(actorInfo.getUri());
					if(checkHealth(actorRef)) 
					{
						return actorRef;
					}
					else
					{
						LOG.debug(actorInfo.getUri() + " is unreachable");
					}
				}
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private boolean checkHealth(ActorRef remoteActor) {
		LOG.debug("PING to " + remoteActor.path().toString());
		try {			
			Future<Object> future =  Patterns.ask(remoteActor, "PING", timeout);
			String result = (String) Await.result(future, timeout.duration());
			LOG.debug(result +  " from " + remoteActor.path().toString());
			return "PONG".equals(result);
		} catch (Throwable t) {
			t.printStackTrace();
			LOG.warn("No Response: " + remoteActor.path().toString());
			return false;
		}
	}
	
	/**
	 * 새로운 액터를 추가한다.
	 * 
	 * @param uri
	 * @param actor
	 */
	public synchronized void addActor(String uri) {
		try {
			if(actorMap.get(uri) == null)
			{
				ActorRef actorRef = getContext().actorFor(uri);
				LOG.info(actorRef.path().toString() + " actor is added...");
				checkHealth(actorRef);
				actorMap.put(uri, actorRef);
			}
		} catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 액터를 삭제한다.
	 * 
	 * @param uri
	 */
	public synchronized void removeActor(String uri) {
		try{
			if(actorMap.containsKey(uri))
			{
				ActorRef actorRef = actorMap.get(uri);
				getContext().stop(actorRef);
				actorMap.remove(uri);
			}
		} catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		
	}

	// private static SupervisorStrategy strategy = new OneForOneStrategy(10,
	// Duration.apply(10, TimeUnit.SECONDS),
	// new Function<Throwable, Directive>() {
	// public Directive apply(Throwable t) {
	// System.out.println("===========================");
	// return resume();
	// }
	// }) {
	//
	// @Override
	// public void processFailure(ActorContext context, boolean restart,
	// ActorRef child, Throwable cause, ChildRestartStats stats,
	// Iterable<ChildRestartStats> children) {
	// System.out.println("++++++++++++++++++++++++++++");
	// System.out.println(child.path().toString());
	// }
	//
	// };
	//
	// @Override
	// public SupervisorStrategy supervisorStrategy() {
	// return strategy;
	// }
	
	/**
	 * 메시지를 받을 때 호출하여 로그 메시지를 남긴다.
	 * @param command
	 */
	@Override
	public void preExecute(TaskCommand command) 
	{
		actorInfo.setStatus(ActorStatus.RUNNING);
		actorInfo.setStart(new Date());
		actorInfo.setCommand(command.getUid().toString());
		actorInfo.setDescription("Message Recieved");
		
		writeLog();
		//ActorManager.getInstance().setUsing(getSelf().path().name(), actorInfo.toJson());
	}
	
	/**
	 * 메시지를 종료할 때 호출하여 로그 메시지를 남긴다.
	 * @param command
	 */
	@Override
	public void postExecute(TaskCommand command) 
	{
		actorInfo.setStatus(ActorStatus.COMPLETED);
		actorInfo.setStart(new Date());
		actorInfo.setCommand(command.getUid().toString());
		actorInfo.setDescription("Message Forwarded");
		
		writeLog();
		//ActorManager.getInstance().setAvailable(getSelf().path().name(), actorInfo.toJson());
	}
}
