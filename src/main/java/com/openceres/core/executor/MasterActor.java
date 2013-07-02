package com.openceres.core.executor;

import java.util.Date;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.RoundRobinRouter;

import com.openceres.core.ActorManager;
import com.openceres.core.command.ResultCommand;
import com.openceres.core.command.TaskCommand;
import com.openceres.core.common.ActorRole;
import com.openceres.core.common.ActorStatus;

public class MasterActor extends MonitorableJobActor
{
	private ActorRef workerRouter;
	private ActorRef transactionRouter;
	
	String hostname;
	
	int nrOfWorkers = 10;
	
	public MasterActor(String uri, String hostname, int nrOfWorkers)
	{
		super(ActorRole.MASTER, uri);
		
		this.hostname = hostname;
		this.nrOfWorkers = nrOfWorkers;
		init();
	}
	
	public MasterActor(String uri, int nrOfWorkers)
	{
		super(ActorRole.MASTER, uri);
		
		this.nrOfWorkers = nrOfWorkers;
		init();
	}
	
	public MasterActor(int nrOfWorkers)
	{
		super(ActorRole.MASTER);
		
		this.nrOfWorkers = nrOfWorkers;
		init();
	}
	
	public void init()
	{
		workerRouter = this.getContext().actorOf(new Props(TaskSingleJobActor.class).withRouter(
		        new RoundRobinRouter(nrOfWorkers)).withDispatcher("as-dispatcher"), "workerRouter");
		
		transactionRouter = this.getContext().actorOf(new Props(TransactionJobActor.class).withRouter(
				new RoundRobinRouter(nrOfWorkers)), "transactionRouter"); 
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof TaskCommand) {
			preExecute((TaskCommand) message);
			TaskCommand command = (TaskCommand) message;
			if (command.getType() == 0) {
				workerRouter.tell(message, getSelf());
			} else if (command.getType() == 1) {
				transactionRouter.tell(message, getSelf());
			}
			postExecute((TaskCommand) message);
		} 
		else if(message instanceof ResultCommand) {
			ResultCommand command = (ResultCommand) message;
			if(!command.isSuccessFlag())
			{
				//TODO exception 처리 추가 
			}
		}
		else if(message instanceof String)
		{
			getContext().sender().tell("PONG", getContext().self());
		}
		else {
			unhandled(message);
		}
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
		actorInfo.setDescription("Message Recieved");
		
		writeLog();
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
		actorInfo.setDescription("Message Transfered");
		
		ActorManager.getInstance().deleteCommandResults(command.getUid().toString());
		
		writeLog();
	}
}
