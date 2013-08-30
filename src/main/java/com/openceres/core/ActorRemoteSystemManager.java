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
		final int nrofworkers = AsConfiguration.getIntValue(Const.AKKA_WORKER_COUNT, 5);

		Config conf = ConfigFactory.load();
		system = ActorSystem.create(systemname, conf.getConfig("master").withFallback(conf));
		
		String hostname = system.settings().config().getString("akka.remote.netty.hostname");
		if(hostname.isEmpty()) {
			hostname = NetworkUtil.getIpAddres();
		}
		int port = system.settings().config().getInt("akka.remote.netty.port");
		String actorname = "master_" + hostname;
		
		final String uri = "akka://" + systemname + "@" + hostname + ":" + port + "/user/" + actorname; 
		
		system.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new MasterActor(uri, nrofworkers); 
			}
		}), actorname); 
		
		LOG.info("Master node [" + uri + "] is started");
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
