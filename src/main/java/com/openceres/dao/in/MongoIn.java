package com.openceres.dao.in;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.openceres.exception.DbException;
import com.openceres.model.TaskInfo;
import com.openceres.property.FrameworkConstant;
import com.openceres.util.MongoUtil;
import com.openceres.util.StringUtils;

public class MongoIn implements In {
	private static final Logger LOG = LoggerFactory.getLogger(MongoIn.class);

	static MongoIn mongoIn = new MongoIn();

	MongoUtil mongoUtil = null;
	
	final static int PAGE_MAX = 50;

	private MongoIn() {
		try {
			mongoUtil = new MongoUtil();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static MongoIn getInstance() {
		return mongoIn;
	}

	public String readTaskForWeb(TaskInfo taskFilter, long startTime, long endTime, 
			int page, int limit, int filter) {
		
		int skip = (page - 1) * limit;
		
		BasicDBObject query = new BasicDBObject();
		query.append("timestamp", new BasicDBObject("$gt", startTime).append("$lt", endTime));
		if (taskFilter != null) {
			if (!StringUtils.isEmpty(taskFilter.getAction())) {
				query.append("action", taskFilter.getAction());
			}
			if (!StringUtils.isEmpty(taskFilter.getCommand())) {
				query.append("command", taskFilter.getCommand());
			}
			if (!StringUtils.isEmpty(taskFilter.getAlarmId())) {
				query.append("alarmId", taskFilter.getAlarmId());
			}
			if (!StringUtils.isEmpty(taskFilter.getAlarmName())) {
				query.append("alarmName", taskFilter.getAlarmName());
			}
			if (!StringUtils.isEmpty(taskFilter.getScheduleId())) {
				query.append("scheduleId", taskFilter.getScheduleId());
			}
			if (!StringUtils.isEmpty(taskFilter.getScheduleName())) {
				query.append("scheduleName", taskFilter.getScheduleName());
			}			
			if (!StringUtils.isEmpty(taskFilter.getPolicyId())) {
				query.append("policyId", taskFilter.getPolicyId());
			}
			if (!StringUtils.isEmpty(taskFilter.getPolicyName())) {
				query.append("policyName", taskFilter.getPolicyName());
			}			
			if (!StringUtils.isEmpty(taskFilter.getUser())) {
				query.append("user", taskFilter.getUser());
			}
		}
		
		if(filter == 0) {
			query.append("description", new BasicDBObject("$ne", "START"));
		} else if(filter == 1) {
			query.append("description", "SUCCESS");
		} else if(filter == 2) {
			List<String> descriptionFilter = new ArrayList<String>();
			descriptionFilter.add("START");
			descriptionFilter.add("SUCCESS");
			query.append("description", new BasicDBObject("$nin", descriptionFilter));
		}

		BasicDBObject field = new BasicDBObject("_id", false);

		BasicDBObject sort = new BasicDBObject("timestamp", -1);
		
		LOG.debug(query.toString());
		if(skip != 0 && limit != 0) {
			return mongoUtil.readAll(
				FrameworkConstant.DB_TASK_COLLECTION, query, field, sort, skip, limit).toString();
		} else if(skip == 0 && limit != 0){
			return mongoUtil.readAll(
					FrameworkConstant.DB_TASK_COLLECTION, query, field, sort, limit).toString();
		} else {
			return mongoUtil.readAll(
					FrameworkConstant.DB_TASK_COLLECTION, query, field, sort).toString();
		}
	}
	
	public long readTaskCount(TaskInfo taskFilter, long startTime, long endTime, int filter)
	{
		BasicDBObject query = new BasicDBObject();
		query.append("timestamp", new BasicDBObject("$gt", startTime).append("$lt", endTime));
		if (taskFilter != null) {
			if (!StringUtils.isEmpty(taskFilter.getAction())) {
				query.append("action", taskFilter.getAction());
			}
			if (!StringUtils.isEmpty(taskFilter.getCommand())) {
				query.append("command", taskFilter.getCommand());
			}
			if (!StringUtils.isEmpty(taskFilter.getAlarmId())) {
				query.append("alarmId", taskFilter.getAlarmId());
			}
			if (!StringUtils.isEmpty(taskFilter.getAlarmName())) {
				query.append("alarmName", taskFilter.getAlarmName());
			}
			if (!StringUtils.isEmpty(taskFilter.getScheduleId())) {
				query.append("scheduleId", taskFilter.getScheduleId());
			}
			if (!StringUtils.isEmpty(taskFilter.getScheduleName())) {
				query.append("scheduleName", taskFilter.getScheduleName());
			}			
			if (!StringUtils.isEmpty(taskFilter.getPolicyId())) {
				query.append("policyId", taskFilter.getPolicyId());
			}
			if (!StringUtils.isEmpty(taskFilter.getPolicyName())) {
				query.append("policyName", taskFilter.getPolicyName());
			}			
			if (!StringUtils.isEmpty(taskFilter.getUser())) {
				query.append("user", taskFilter.getUser());
			}
		}
		
		if(filter == 0) {
			query.append("description", new BasicDBObject("$ne", "START"));
		} else if(filter == 1) {
			query.append("description", "SUCCESS");
		} else if(filter == 2) {
			List<String> descriptionFilter = new ArrayList<String>();
			descriptionFilter.add("START");
			descriptionFilter.add("SUCCESS");
			query.append("description", new BasicDBObject("$nin", descriptionFilter));
		}

		return mongoUtil.getRecordCount(FrameworkConstant.DB_TASK_COLLECTION, query);
	}
	
	private ImmutableList<DBObject> readTaskAll(TaskInfo taskFilter, long startTime, long endTime, 
			int skip, int limit) {
		BasicDBObject query = new BasicDBObject();
		query.append("timestamp", new BasicDBObject("$gt", startTime).append("$lt", endTime));
		if (taskFilter != null) {
			if (!StringUtils.isEmpty(taskFilter.getAction())) {
				query.append("action", taskFilter.getAction());
			}
			if (!StringUtils.isEmpty(taskFilter.getCommand())) {
				query.append("command", taskFilter.getCommand());
			}
			if (!StringUtils.isEmpty(taskFilter.getAlarmId())) {
				query.append("alarmId", taskFilter.getAlarmId());
			}
			if (!StringUtils.isEmpty(taskFilter.getAlarmName())) {
				query.append("alarmName", taskFilter.getAlarmName());
			}
			if (!StringUtils.isEmpty(taskFilter.getScheduleId())) {
				query.append("scheduleId", taskFilter.getScheduleId());
			}
			if (!StringUtils.isEmpty(taskFilter.getScheduleName())) {
				query.append("scheduleName", taskFilter.getScheduleName());
			}			
			if (!StringUtils.isEmpty(taskFilter.getPolicyId())) {
				query.append("policyId", taskFilter.getPolicyId());
			}
			if (!StringUtils.isEmpty(taskFilter.getPolicyName())) {
				query.append("policyName", taskFilter.getPolicyName());
			}			
			if (!StringUtils.isEmpty(taskFilter.getUser())) {
				query.append("user", taskFilter.getUser());
			}
		}

		BasicDBObject field = new BasicDBObject("_id", false);

		BasicDBObject sort = new BasicDBObject("timestamp", -1);
		
		LOG.debug(query.toString());
		if(skip != 0 && limit != 0) {
			return mongoUtil.readAll(
					FrameworkConstant.DB_TASK_COLLECTION, query, field, sort, skip, limit);
		} else if(skip == 0 && limit != 0){
			return mongoUtil.readAll(
					FrameworkConstant.DB_TASK_COLLECTION, query, field, sort, limit);
		} else {
			return mongoUtil.readAll(
					FrameworkConstant.DB_TASK_COLLECTION, query, field, sort);
		}
	}
	
	@Override
	public String readTasksToJson(TaskInfo taskFilter, long startTime, long endTime) {
		ImmutableList<DBObject> logList = readTaskAll(taskFilter, startTime, endTime, 0, PAGE_MAX);
		
		return logList.toString();
	}
	
	@Override
	public String readTasksToJson(TaskInfo taskFilter, long startTime, long endTime, int page, int limit)
	{
		int skip = (page - 1) * limit;
		ImmutableList<DBObject> logList = readTaskAll(taskFilter, startTime, endTime, skip, limit);
		
		return logList.toString();
	}
	
	@Override
	public List<TaskInfo> readTasks(TaskInfo taskFilter, long startTime, long endTime) {
		
		ImmutableList<DBObject> logList = readTaskAll(taskFilter, startTime, endTime, 0, PAGE_MAX);

		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (DBObject log : logList) {
			try {
				taskInfos.add(toTaskInfoFromJson(log.toString()));
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}

		return taskInfos;
	}
	
	@Override
	public List<TaskInfo> readTasks(TaskInfo taskFilter, long startTime, long endTime, int page, int limit)
	{
		int skip = (page - 1) * limit;

		ImmutableList<DBObject> logList = readTaskAll(taskFilter, startTime, endTime, skip, limit);
		
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (DBObject log : logList) {
			try {
				taskInfos.add(toTaskInfoFromJson(log.toString()));
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}

		return taskInfos;
	}

	@Override
	public List<TaskInfo> readTasksWithCommand(String commandId) {
		BasicDBObject query = new BasicDBObject();
		query.append("command", commandId);

		BasicDBObject field = new BasicDBObject("_id", false);

		BasicDBObject sort = new BasicDBObject("timestamp", -1);

		LOG.debug(query.toString());
		ImmutableList<DBObject> logList = mongoUtil.readAll(
				FrameworkConstant.DB_TASK_COLLECTION, query, field, sort);

		List<TaskInfo> actorInfos = new ArrayList<TaskInfo>();
		for (DBObject log : logList) {
			try {
				actorInfos.add(toTaskInfoFromJson(log.toString()));
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}

		return actorInfos;

	}

	private TaskInfo toTaskInfoFromJson(String jsonString) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		TaskInfo taskInfo = mapper.readValue(jsonString, TaskInfo.class);

		return taskInfo;
	}
	
	public static void main(String args[])
	{
		MongoIn mongoIn = new MongoIn();
		String result = mongoIn.readTaskCount(null, 0L, Long.MAX_VALUE, 2) + "";
		System.out.println(result);
	}
}
