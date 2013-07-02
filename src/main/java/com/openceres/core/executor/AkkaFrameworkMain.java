package com.openceres.core.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

import com.openceres.core.ActorManager;
import com.openceres.property.FrameworkConstant;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AkkaFrameworkMain {
	private static Logger LOG = LoggerFactory.getLogger(AkkaFrameworkMain.class);

	private static AkkaFrameworkMain akkaFramwork = null;
	
	ActorSystem system = null;
	ActorRef proxyActorRef = null;
	
	
	private AkkaFrameworkMain() 
	{
		init();
	}
	
	public static AkkaFrameworkMain getInstance()
	{
		if(akkaFramwork == null)
		{
			LOG.info("AkkaFrame created!!!");
			akkaFramwork = new AkkaFrameworkMain();
		}
		
		return akkaFramwork;
	}
	
	private void init() {	
		ActorManager.getInstance().start();
		
		String confString =   
				"akka {\n" + 
				  "actor {\n" +
					    "provider = \"akka.remote.RemoteActorRefProvider\"\n" +
					  "}\n" +
				"}";
		Config conf = ConfigFactory.parseString(confString);
		system = ActorSystem.create(FrameworkConstant.AKKA_MAIN_SYSTEM, ConfigFactory.load(conf));

		//final String[] addresses = {"masterActor1", "masterActor2"};
		
		//create proxy actor
		proxyActorRef = system.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new ProxyActor();
			}
		}), "proxyActor");
	}
	
	public ActorRef getProxyActor()
	{
		return proxyActorRef;
	}
	
	public ActorSystem getActorSystem() {
		return system;
	}
}
