package com.openceres.example.helloworld.action;

import java.util.Map;

import org.apache.tools.ant.BuildException;

import com.openceres.core.action.ActionTask;

public class HelloTask extends ActionTask{

	Map<String, Object> params;
	
	@Override
	protected boolean checkParam() {
		params = command.getParemeterMap();
		if(params != null) {
			if(params.containsKey("name")) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void execute() throws BuildException {
		if(!checkParam()) {
			System.out.println("Parameter Exception");
			throw new BuildException("Parameter Exception");
		}
		
		System.out.println("Hello, " + params.get("name"));
	}
	
}
