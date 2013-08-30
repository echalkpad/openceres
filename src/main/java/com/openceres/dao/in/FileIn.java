package com.openceres.dao.in;

import java.util.List;
import com.openceres.model.ActorInfo;

public class FileIn implements In{

	static FileIn fileIn = new FileIn();
	
	final static int PAGE_MAX = 50;

	private FileIn() {
	}

	public static FileIn getInstance() {
		return fileIn;
	}
	
	@Override
	public List<ActorInfo> readLog(ActorInfo actorInfoFilter, long startTime,
			long endTime) {
		//Not supported yet.
		return null;
	}

}
