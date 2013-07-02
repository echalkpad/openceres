package com.openceres.core;

import org.apache.camel.CamelContext;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.impl.DefaultCamelContext;

import com.openceres.config.AsConfiguration;
import com.openceres.model.Email;
import com.openceres.property.Const;

public class QueueInputer {
	private static CamelContext camelContext;
	
	public static void init() {
		camelContext = new DefaultCamelContext();
		camelContext.addComponent("jms", ActiveMQComponent.activeMQComponent(AsConfiguration.getValue(Const.GLOBAL_QUEUE_CONNECTION)));
		
		try {
			camelContext.addRoutes(new RouteBuilder(){
				@Override
				public void configure() throws Exception {
					from("direct:email")
					.to("jms:queue:email");
					
				}
			});
			
			camelContext.addRoutes(new RouteBuilder(){
				@Override
				public void configure() throws Exception {
					from("direct:sms")
					.to("jms:queue:sms");
					
				}
			});
			
			camelContext.start();
			
		} catch (Exception e) {
			//LOG.error(e.getMessage(), e);
			throw new RuntimeCamelException(e);
		}
		
	}
	
	public static void mailInputer(Email email) {
		camelContext.createProducerTemplate().sendBody("direct:email", email);
	}
}
