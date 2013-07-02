package com.openceres.core.executor;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.kernel.Bootable;

import com.openceres.core.ActorManager;
import com.openceres.property.FrameworkConstant;
import com.openceres.util.NetworkUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AkkaDaemon implements Bootable{
	
	ActorSystem system = null;

	public void shutdown() {
		system.shutdown();
		
	}

	@SuppressWarnings("serial")
	public void startup() {
		ActorManager.getInstance().start();
		final String systemname = FrameworkConstant.AKKA_SUB_SYSTEM;
		final String hostname  = NetworkUtil.getIpAddres();
		final int port = 2555;
		final String actorname = "masterActor";
		final int nrofworkers = 5;
		
		String confString =   
			"akka {\n" + 
			  "actor {\n" +
				    "provider = \"akka.remote.RemoteActorRefProvider\"\n" +
				  "}\n" +
				  "remote { \n" +
				    "transport = \"akka.remote.netty.NettyRemoteTransport\"\n" +
				    "netty {\n" + 
				      "hostname = \"" + hostname + "\"\n" +
				      "port = " + port + "\n" +
				    "}\n" +
				  "}\n" +
				"}\n";
		
		System.out.println(confString);
		Config conf = ConfigFactory.parseString(confString);
		
		system = ActorSystem.create(systemname, ConfigFactory.load(conf));
		
		final String uri = "akka://" + systemname + "@" + hostname + ":" + port + "/user/" + actorname; 
		
		system.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new MasterActor(uri, nrofworkers); 
			}
		}), actorname);  
	}
	
	public static void main(String args[]) throws Exception
	{
		AkkaDaemon akkaDaemon = new AkkaDaemon();
		akkaDaemon.startup();
	}

}
