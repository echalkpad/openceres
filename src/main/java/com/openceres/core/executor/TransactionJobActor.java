package com.openceres.core.executor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.AllForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.japi.Function;
import akka.pattern.Patterns;
import akka.transactor.Coordinated;
import akka.transactor.CoordinatedTransactionException;
import akka.util.Timeout;

import com.openceres.core.ActorManager;
import com.openceres.core.command.Command;
import com.openceres.core.command.ResultCommand;
import com.openceres.core.command.TaskCommand;
import com.openceres.core.common.ActorRole;
import com.openceres.core.common.ActorStatus;

/**
 * 
 * <h1>TransactionJobActor</h1>
 * 트랜잭션이 필요한 작업을 관리하는 actor
 * 
 * @author changbaechoi
 *
 */
public class TransactionJobActor extends MonitorableJobActor{
	private static final Logger LOG = LoggerFactory.getLogger(TransactionJobActor.class);
	

	
	//TODO timeout setting 하기 
	Timeout timeout = new Timeout(10, TimeUnit.SECONDS);
	
	public TransactionJobActor()
	{
		super(ActorRole.SUPERVISOR);
	}

	@Override
	public void onReceive(final Object message) throws Exception {
		final List<ActorRef> actorList = new ArrayList<ActorRef>();
		final List<TaskCommand> commandList = new ArrayList<TaskCommand>(); 
		
		preExecute((TaskCommand)message);
		if (message instanceof Command) {
			final Coordinated coordinated = new Coordinated(timeout);
			coordinated.atomic(new Runnable() {
				@SuppressWarnings("serial")
				public void run()
				{
					TaskCommand command = (TaskCommand)message;
					int count = 0;
					
					while(command != null){
						count++;
						ActorRef actorRef =  getContext().actorOf(new Props(new UntypedActorFactory() {
							public UntypedActor create() {
								return new TaskSingleJobActor();
							}
						}), "transation_worker" + count);
						actorList.add(actorRef);
						commandList.add(command);
						command = command.getChild();
					};
					
					int fullProcessCount = actorList.size();
					int idx = 0;
					ResultCommand resultCommand = null;
					for(ActorRef actorRef : actorList)
					{
						try {
							command = commandList.get(idx);
							if(resultCommand != null)
							{
								if( resultCommand.isSuccessFlag() ){
									if(resultCommand.getResultMap() != null)
									{
										resetCommandParam(command, resultCommand);
									}
								} else {
									LOG.info(actorRef.path().toString() + "...STOP");
									break;
								}
							}
							processing(command, (idx + 1) + "/" + fullProcessCount);
							Future<Object> future = Patterns.ask(actorRef, command, timeout);
							Object result = Await.result(future, timeout.duration());
							idx++;
							
							if(result instanceof ResultCommand)
							{
								resultCommand = (ResultCommand)result;
							}
							LOG.info(actorRef.path().toString() + "...DONE");
						} catch (Throwable t) {
							t.printStackTrace();
							LOG.error(actorRef.path().toString() + "...FAIL", t);
							unhandled(message);
							//TODO 에러처리 
							//errorActorRefs.add(new ErrorActorDelayed(index));
							break;
						}
					}
					
					//생성 된 Actor 들은 종료시킨다. 
					for(ActorRef actorRef : actorList)
					{
						getContext().stop(actorRef);
					}
					
					getSender().tell(resultCommand, null);
				}
			});
		} else {
			unhandled(message);
		}
		postExecute((TaskCommand)message);
	}
	
	private void resetCommandParam(TaskCommand taskCommand, ResultCommand resultCommand)
	{
		Map<String, Object> paramMap = taskCommand.getParemeterMap();
		if(paramMap == null)
		{
			paramMap = new HashMap<String, Object>();
		}
		Map<String, Object> resultMap = resultCommand.getResultMap();
		Iterator<String> iter = resultMap.keySet().iterator();
		
		while(iter.hasNext())
		{
			String key = iter.next();
			if(resultMap.containsKey(key))
			{
				paramMap.put(key, resultMap.get(key));
			}
		} 
		taskCommand.setParemeterMap(paramMap);
	}
	
	private static SupervisorStrategy strategy = new AllForOneStrategy(10, Duration.create("10 second"),
			new Function<Throwable, Directive>() {
		public Directive apply(Throwable t) {
			if(t instanceof CoordinatedTransactionException) {
				return SupervisorStrategy.resume();
			} else if (t instanceof IllegalStateException) {
				return SupervisorStrategy.resume();
			} else if (t instanceof IllegalArgumentException) {
				return SupervisorStrategy.stop();
			} else {
				return SupervisorStrategy.escalate();
			}
		}
	});
	
	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}
	
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
		actorInfo.setDescription("Job Starting");
		
		writeLog();
		ActorManager.getInstance().setData(getSelf().path().name(), actorInfo);
	}
	
	/**
	 * 메시지를 받을 때 호출하여 로그 메시지를 남긴다.
	 * @param command
	 */
	public void processing(TaskCommand command, String procedure) 
	{
		actorInfo.setStatus(ActorStatus.RUNNING);
		actorInfo.setStart(new Date());
		actorInfo.setCommand(command.getUid().toString());
		actorInfo.setDescription("Job Starting");
		
		writeLog();
		ActorManager.getInstance().setData(getSelf().path().toString(), actorInfo);
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
		actorInfo.setDescription("Job Completed");
		
		writeLog();
		ActorManager.getInstance().setData(getSelf().path().toString(), actorInfo);
	}
}
