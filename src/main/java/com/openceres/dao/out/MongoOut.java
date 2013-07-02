package com.openceres.dao.out;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.openceres.config.AsConfiguration;
import com.openceres.exception.DbException;
import com.openceres.model.TaskInfo;
import com.openceres.property.Const;
import com.openceres.property.FrameworkConstant;
import com.openceres.util.MongoUtil;
import com.openceres.util.StringUtils;

public class MongoOut implements Out {
	static MongoOut mongoPerfOut = new MongoOut();

	MongoUtil mongoUtil = null;

	private MongoOut() {
		try {
			mongoUtil = new MongoUtil();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static MongoOut getInstance() {
		return mongoPerfOut;
	}

	@SuppressWarnings("unchecked")
	public void writeTask(TaskInfo taskInfo) {
		// System.out.println(taskInfo.toJson());
		BasicDBObject query = new BasicDBObject();
		if (StringUtils.isEmpty(taskInfo.getScheduleId()) && StringUtils.isEmpty(taskInfo.getAlarmId())) {
			query.put("command", taskInfo.getCommand());
			DBObject dbObject = mongoUtil.read(AsConfiguration.getValue(Const.DB_MONGO_TASK_LOG_COLLECTION),
					query, null);
			if (dbObject != null) {
				Map<String, Object> taskMap = dbObject.toMap();
				if (taskMap.containsKey("policyId")) {
					taskInfo.setPolicyId((String) taskMap.get("policyId"));
				}
				if (taskMap.containsKey("policyName")) {
					taskInfo.setPolicyName((String) taskMap.get("policyName"));
				}
				if (taskMap.containsKey("scheduleId")) {
					taskInfo.setScheduleId((String) taskMap.get("scheduleId"));
				}
				if (taskMap.containsKey("scheduleName")) {
					taskInfo.setScheduleName((String) taskMap.get("scheduleName"));
				}
				if (taskMap.containsKey("alarmId")) {
					taskInfo.setAlarmId((String) taskMap.get("alarmId"));
				}
				if (taskMap.containsKey("alarmName")) {
					taskInfo.setAlarmName((String) taskMap.get("alarmName"));
				}
			}
		}

		mongoUtil.insert(FrameworkConstant.DB_TASK_COLLECTION, taskInfo.toJson());
	}


	@SuppressWarnings("unused")
	private TaskInfo toTaskInfoFromJsoOn(String jsonString) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		TaskInfo taskInfo = mapper.readValue(jsonString, TaskInfo.class);

		return taskInfo;
	}

}
