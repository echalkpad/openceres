package com.openceres.core.router.endpoint;

import akka.camel.javaapi.UntypedProducerActor;

public class MailProducer extends UntypedProducerActor {

	@Override
	public String getEndpointUri() {
		return "jms:queue:email";
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
