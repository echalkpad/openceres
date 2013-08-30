package com.openceres.core;

import org.apache.camel.CamelContext;

public interface SystemManager {
	public void init();
	public void startup();
	public void shutdown();
	public CamelContext getCamelContext();
}
