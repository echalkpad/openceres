package com.openceres.core.command;

import java.util.UUID;

public class TimerBaseCommand extends BaseCommand{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8202043948532433014L;
	
	long 	startTime;
	int		timeout;		//Second
	
	public TimerBaseCommand(UUID uid) {
		super(uid);
		startTime = System.currentTimeMillis();
		this.timeout = 0;
	}
	
	public TimerBaseCommand(UUID uid, int timeout)
	{
		this(uid);
		this.timeout = timeout;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public long getStartTime() {
		return startTime;
	}
}
