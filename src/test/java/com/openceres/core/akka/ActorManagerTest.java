package com.openceres.core.akka;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openceres.core.ActorManager;
import com.openceres.model.ActorInfo;

public class ActorManagerTest {
	private static final Logger logger = LoggerFactory.getLogger(ActorManagerTest.class);
	
	@Test
	public void testGetData() {
		logger.info("abc");
		List<ActorInfo> data = ActorManager.getInstance().getAllData();
		
		for(ActorInfo datum : data)
		{
			logger.info(datum.toJson());
		}
	}

}
