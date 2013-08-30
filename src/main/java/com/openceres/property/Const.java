package com.openceres.property;

public class Const {
	public static String MODULE_NAME = "";
	
	public final static String LOGGER_DB = "global.log.db";

	// MongoDB
	public final static String DB_MONGO_DEFAULT_SERVER = "global.mongo.servers";
	public final static String DB_MONGO_DEFAULT_PORT = "global.mongo.servers.port";
	public final static String DB_MONGO_DEFAULT_DBNAME = "global.mongo.db.name";
	public final static String DB_MONGO_DEFAULT_WRITE_CONCERN = "global.mongo.db.write.concern";

	public final static String DB_MONGO_LOG_COLLECTION = "global.log.actor.table";
	
	// Global queue
	public final static String GLOBAL_QUEUE_CONNECTION = "global.queue.connection";
	
	public final static String ZK_SERVERS = "zk.servers";
	public final static String MON_PORT = "as.mon.port";
	public final static String AKKA_WORKER_COUNT = "as.worker.count";
		
	// Property files
	public final static String PROP_FILE_QUARTZ = "conf/quartz.properties";
	public final static String PROP_FILE_LOGGER = "conf/log4j.properties";
	
}
