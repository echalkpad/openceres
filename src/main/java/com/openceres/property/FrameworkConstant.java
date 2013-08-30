package com.openceres.property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openceres.config.AsConfiguration;

public class FrameworkConstant {
	private static final Logger LOG = LoggerFactory.getLogger(FrameworkConstant.class);
	
	public static String MODULE_NAME = "";

	// AKKA framework
	public final static String AKKA_MAIN_SYSTEM = "MiDE";
	public final static String AKKA_SUB_SYSTEM = "SubMiDE";

	public static void setConfigure() {
		AsConfiguration asConfiguration = AsConfiguration.getInstance();
		asConfiguration.startup();

		try {
			String zkServers = asConfiguration.get("zk.servers");
			if (zkServers == null) {
				LOG.error("EXIT: Zookeeper node does not set...");
				System.exit(0);
			}
			if (MODULE_NAME.equalsIgnoreCase("akka")) {
				asConfiguration.upload();
			}
			asConfiguration.download();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
