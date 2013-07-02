package com.openceres.core;

import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.kernel.Bootable;

import com.openceres.config.AsConfiguration;
import com.openceres.core.executor.MasterActor;
import com.openceres.property.Const;
import com.openceres.property.FrameworkConstant;
import com.openceres.util.NetworkUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ActorRemoteSystemManager implements Bootable, SystemManager {
	private static final Logger LOG = LoggerFactory.getLogger(ActorRemoteSystemManager.class);
	
	ActorSystem system = null;
	
	private static final ActorRemoteSystemManager actorRemoteSystemManager = new ActorRemoteSystemManager();
	
	private ActorRemoteSystemManager() {
		QueueInputer.init();
	}
	
	public static ActorRemoteSystemManager getInstance() {
		return actorRemoteSystemManager;
	}

	@Override
	public void startup() {
		init();
	}

	@SuppressWarnings("serial")
	@Override
	public void init() {
		LOG.info("Initialize ActorRemoteSystemManager...");
		
		ActorManager.getInstance().start();
		final String systemname = FrameworkConstant.AKKA_SUB_SYSTEM;
		final String hostname  = NetworkUtil.getIpAddres();
		final int port = 2555;
		final String actorname = "master_" + hostname;
		final int nrofworkers = AsConfiguration.getIntValue(Const.AKKA_WORKER_COUNT, 5);

		
		String confString =   
			"akka {\n" + 
				"\tactor {\n" +
					"\t\tprovider = \"akka.remote.RemoteActorRefProvider\"\n" +
				"\t}\n" +
				"\tremote { \n" +
					"\t\ttransport = \"akka.remote.netty.NettyRemoteTransport\"\n" +
				    "\t\tnetty {\n" + 
				    	"\t\t\thostname = \"" + hostname + "\"\n" +
				    	"\t\t\tport = " + port + "\n" +
				    "\t\t}\n" +
			    "\t}\n" +
			    "\tas-dispatcher {\n" +
					"\t\ttype = \"BalancingDispatcher\"\n" +
					"\t\texecutor = \"fork-join-executor\"\n" +
					"\t\tthread-pool-executor { \n" +
				  		"\t\t\tcore-pool-size-min = 2\n" +
				  		"\t\t\tcore-pool-size-factor = 2.0\n" +
				  		"\t\t\tcore-pool-size-max = 10\n" +
				  	"\t\t}\n" +
				  	"\t\tthroughput = 100\n" +
				  	"\t\tmailbox-capacity = -1\n" +
				  	"\t\tmailbox-type = \"\"\n" +
			  	"\t}\n" +
			"}\n";
		
		
		Config conf = ConfigFactory.parseString(confString);
		
		system = ActorSystem.create(systemname, ConfigFactory.load(conf));
		
		final String uri = "akka://" + systemname + "@" + hostname + ":" + port + "/user/" + actorname; 
		
		system.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new MasterActor(uri, hostname, nrofworkers); 
			}
		}), actorname); 
		
		LOG.info("Master node [" + uri + "] is started");
		LOG.debug("Configuration : \n" + confString);
		
	}
	
	@Override
	public void shutdown() {
		LOG.info("Shutdown ActorRemoteSystemManager...");
		system.shutdown();
		
	}

	@Override
	public CamelContext getCamelContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
