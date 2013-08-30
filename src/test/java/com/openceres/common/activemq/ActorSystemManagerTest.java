package com.openceres.common.activemq;

import org.apache.camel.Component;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.camel.Camel;
import akka.camel.CamelExtension;

import com.openceres.core.ActorSystemManager;

public class ActorSystemManagerTest {

	@Test
	public void test() {
		ActorSystem actorSystem = ActorSystemManager.getInstance().getActorSystem();
		Camel camel = CamelExtension.get(actorSystem);
		Component component  = camel.context().hasComponent("jms");
		if(component != null)
		{
			System.out.println("Delete Component");
			camel.context().removeComponent("jms");
		}
	}

}
