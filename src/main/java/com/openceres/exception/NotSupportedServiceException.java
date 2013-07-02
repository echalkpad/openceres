package com.openceres.exception;

import org.apache.tools.ant.BuildException;

public class NotSupportedServiceException extends BuildException {

	private static final long serialVersionUID = 1L;
	
	public NotSupportedServiceException(String msg)
	{
		super(msg);
	}

}
