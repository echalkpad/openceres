package com.openceres.dao.out;

import com.openceres.model.TaskInfo;

public interface Out {

	/**
	 * 태스크 데이터를 입력한다. 
	 * 
	 * @param taskInfo	태스크 정보 
	 */
	public void writeTask(TaskInfo taskInfo);
	

}
