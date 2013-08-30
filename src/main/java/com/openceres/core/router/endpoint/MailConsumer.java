package com.openceres.core.router.endpoint;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.camel.CamelMessage;
import akka.camel.javaapi.UntypedConsumerActor;

import com.openceres.model.Email;

public class MailConsumer extends UntypedConsumerActor {
	private static final Logger LOG = LoggerFactory.getLogger(MailConsumer.class);
	
	@Override
	public String getEndpointUri() {
		return "jms:queue:email";
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof CamelMessage) {
			CamelMessage camelMessage = (CamelMessage) message;
			Email email = (Email)camelMessage.body();
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("To", email.getRecipients());
			map.put("From", email.getSender());
			map.put("Subject", email.getSubject());
			
			LOG.info("Sending email from " + email.getSender() + " to " + email.getRecipients());
			
			String body = email.getContents();
			
			camelContext().createProducerTemplate().sendBodyAndHeaders("smtp://dima@tcloud.micloud.kr?password=dima", body, map);
		} else {
			unhandled(message);
		}
	}
}
