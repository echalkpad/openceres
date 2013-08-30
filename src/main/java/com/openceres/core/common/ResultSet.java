package com.openceres.core.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.openceres.util.Parser;

public class ResultSet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2013L;

	/**
	 * Task 의 성공 여부 설정 
	 */
	public boolean isSuccess;
	
	/**
	 * 결과를 리턴하기 위한 저장소 
	 */
	public Map<String, Object> resultMap = new HashMap<String, Object>();
	
	public byte[] toByteArray()
	{
		return  Parser.toByteArray(this);
	}
			
}
