package com.openceres.core;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.camel.Camel;
import akka.camel.CamelExtension;
import akka.kernel.Bootable;

import com.openceres.config.AsConfiguration;
import com.openceres.core.executor.ProxyActor;
import com.openceres.core.router.endpoint.CmdConsumer;
import com.openceres.core.router.endpoint.CmdProducer;
import com.openceres.property.Const;
import com.openceres.property.FrameworkConstant;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


public class ActorSystemManager implements Bootable, SystemManager {
	private static final Logger LOG = LoggerFactory.getLogger(ActorSystemManager.class);

	private ActorSystem actorSystem;
	private ActorRef proxyActorRef = null;
	private CamelContext camelContext;

	private static final ActorSystemManager actorSystemManager = new ActorSystemManager();

	private ActorSystemManager() {
		QueueInputer.init();
	}

	public static ActorSystemManager getInstance() {
		return actorSystemManager;
	}

	@SuppressWarnings("serial")
	@Override
	public void init() {
		LOG.info("Initialize ActorSystemManager...");

		ActorManager.getInstance().start();

		Config conf = ConfigFactory.load();
		actorSystem = ActorSystem.create(FrameworkConstant.AKKA_MAIN_SYSTEM, conf.getConfig("proxy").withFallback(conf));

		// create proxy actor
		proxyActorRef = actorSystem.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new ProxyActor();
			}
		}), "proxyActor");
		
		Camel camel = CamelExtension.get(actorSystem);
		camelContext = camel.context();
		camelContext.addComponent("jms",
				ActiveMQComponent.activeMQComponent(AsConfiguration.getValue(Const.GLOBAL_QUEUE_CONNECTION)));
		
//		try {
//			camelContext.addRoutes(new RouteBuilder(){
//				@Override
//				public void configure() throws Exception {
//					from("direct:email")
//					.to("jms:queue:email");
//					
//				}});
//		} catch (Exception e1) {
//			LOG.error(e1.getMessage(), e1);
//		}

		actorSystem.actorOf(new Props(CmdConsumer.class), "cmdConsumer");
		actorSystem.actorOf(new Props(CmdProducer.class), "cmdProducer");
		// actorSystem.actorOf(new Props(MailConsumer.class), "mailConsumer");
//		actorSystem.actorOf(new Props(MailProducer.class), "mailProducer");
		
//		OpenCeresWas was = new OpenCeresWas();
//		
//		try 
//		{
//			was.start();
//		}
//		catch(Exception e)
//		{
//			LOG.error(e.getMessage(), e);
//		}
	}

	@Override
	public void shutdown() {
		LOG.info("Shutdown ActorSystemManager...");
		actorSystem.stop(proxyActorRef);
		actorSystem.shutdown();
	}

	@Override
	public void startup() {
		init();
	}

	public ActorSystem getActorSystem() {
		return actorSystem;
	}

	@Override
	public CamelContext getCamelContext() {
		return camelContext;
	}
}
