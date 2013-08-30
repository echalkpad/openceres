package com.openceres.core.command;

import java.util.Map;
import java.util.UUID;

public class ResultCommand extends BaseCommand
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2013L;
	
	/**
	 * 결과로 전달해야 할 값들을 저장한다. 
	 */
	Map<String, Object> resultMap;
	
	/**
	 * 더 수행해야 할 명령이 있는 경우 계속 수행할지 여부를 설정한다. 
	 */
	boolean continueFlag;
	
	/**
	 * 명령이 성공했는지 여부를 설정한다. 
	 */
	boolean successFlag;
	
	public ResultCommand(UUID uid) {
		super(uid);
		// TODO Auto-generated constructor stub
	}

	public boolean isContinueFlag() {
		return continueFlag;
	}

	public void setContinueFlag(boolean continueFlag) {
		this.continueFlag = continueFlag;
	}

	public boolean isSuccessFlag() {
		return successFlag;
	}

	public void setSuccessFlag(boolean successFlag) {
		this.successFlag = successFlag;
	}

	public Map<String, Object> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}
}
