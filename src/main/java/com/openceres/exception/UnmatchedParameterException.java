package com.openceres.exception;

import org.apache.tools.ant.BuildException;

public class UnmatchedParameterException extends BuildException {

	private static final long serialVersionUID = 1L;
	
	public UnmatchedParameterException(String msg)
	{
		super(msg);
	}

}
