package com.openceres.core.command;

import java.util.UUID;

public class BaseCommand implements Command{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2013L;
	
	final UUID uid;

	public BaseCommand(UUID uid)
	{
		this.uid = uid;
	}
	
	public UUID getUid() 
	{
		return uid;
	}
}
