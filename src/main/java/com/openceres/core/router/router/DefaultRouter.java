package com.openceres.core.router.router;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;

public class DefaultRouter extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("direct:command")
		.to(ExchangePattern.InOnly, "jms:queue:command");
	}
}
