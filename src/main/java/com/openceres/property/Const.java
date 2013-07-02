package com.openceres.property;

public class Const {
	public static String MODULE_NAME = "";
	
	public final static String PERF_OUTER = "global.log.db";
	public final static String PERF_INNER = "global.log.db";
	
	public final static String TASK_OUTER = "global.log.db";
	public final static String TASK_INNER = "global.log.db";

	// MongoDB
	public final static String DB_MONGO_DEFAULT_SERVER = "global.mongo.servers";
	public final static String DB_MONGO_DEFAULT_PORT = "global.mongo.servers.port";
	public final static String DB_MONGO_DEFAULT_DBNAME = "global.mongo.db.name";
	public final static String DB_MONGO_DEFAULT_WRITE_CONCERN = "global.mongo.db.write.concern";

	public final static String DB_MONGO_LOG_COLLECTION = "global.log.actor.table";
	public final static String DB_MONGO_PERF_COLLECTION = "global.log.perf.table";
	public final static String DB_MONGO_TASK_LOG_COLLECTION = "global.log.task.table";
	
	// Global queue
	public final static String GLOBAL_QUEUE_CONNECTION = "global.queue.connection";
	
	public final static String ZK_SERVERS = "zk.servers";
	public final static String MON_PORT = "as.mon.port";
	public final static String AKKA_WORKER_COUNT = "as.worker.count";
	
	//Default Monitoring Value
	public final static String DEFAULT_SYSTEM_USER = "as.system.user";
	public final static String DEFAULT_MON_ACTION = "as.default.monitor.action";
	public final static String DEFAULT_MON_INTERVAL = "as.default.monitor.interval";
	
	// Property files
	public final static String PROP_FILE_QUARTZ = "conf/quartz.properties";
	public final static String PROP_FILE_JDBC = "conf/jdbc.properties";
	
	// SMS Defualt sender
	public final static String NOTI_DEFAULT_SMS_SENDER = "global.noti.sms.sender";
	public final static String NOTI_DEFAULT_EMAIL_SENDER = "global.noti.email.sender";
	
	
	//FIXME Default Resource type
	public final static String RESOURCE_DEFAULT_NIC = "global.alarm.default.nic";
	public final static String RESOURCE_DEFAULT_DISK = "global.alarm.default.disk";
}
