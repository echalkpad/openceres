package com.openceres.core.router.endpoint;

import akka.camel.javaapi.UntypedProducerActor;

public class CmdProducer extends UntypedProducerActor {

	@Override
	public String getEndpointUri() {
		return "jms:queue:command";
	}
	
	@Override
	public Object onTransformOutgoingMessage(Object message) {
		return super.onTransformOutgoingMessage(message);
	}
	
	@Override
	public Object onTransformResponse(Object message) {
		return super.onTransformResponse(message);
	}
	
	@Override
	public boolean isOneway() {
		return true;
	}
}
