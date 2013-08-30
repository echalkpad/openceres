package com.openceres.core.router.endpoint;

import akka.camel.CamelMessage;
import akka.camel.javaapi.UntypedConsumerActor;

import com.openceres.property.FrameworkConstant;

public class CmdConsumer extends UntypedConsumerActor {
	
	@Override
	public String getEndpointUri() {
		return "jms:queue:command";
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof CamelMessage) {
			CamelMessage camelMessage = (CamelMessage)message;
			context().actorFor("akka://" + FrameworkConstant.AKKA_MAIN_SYSTEM + "/user/proxyActor").tell(camelMessage.body(), null);
		} else {
			unhandled(message);
		}
	}

}
