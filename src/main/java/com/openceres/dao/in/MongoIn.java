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
import com.openceres.config.AsConfiguration;
import com.openceres.core.common.ActorRole;
import com.openceres.core.common.ActorStatus;
import com.openceres.exception.DbException;
import com.openceres.model.ActorInfo;
import com.openceres.property.Const;
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

	public List<ActorInfo> readLog(ActorInfo actorInfoFilter, long startTime, long endTime) {
		BasicDBObject query = new BasicDBObject();
		query.append("start", new BasicDBObject("$gt", startTime).append("$lt", endTime));
		if (actorInfoFilter != null) {
			if (actorInfoFilter.getRole() != ActorRole.NONE) {
				query.append("role", actorInfoFilter.getRole().name());
			}
			if (!StringUtils.isEmpty(actorInfoFilter.getCommand())) {
				query.append("command", actorInfoFilter.getCommand());
			}
			if (actorInfoFilter.getStatus() != ActorStatus.INIT) {
				query.append("status", actorInfoFilter.getStatus().name());
			}
		}

		BasicDBObject field = new BasicDBObject("_id", false);
		BasicDBObject sort = new BasicDBObject("start", -1);

		LOG.debug(query.toString());
		ImmutableList<DBObject> logList = mongoUtil.readAll(
				AsConfiguration.getValue(Const.DB_MONGO_LOG_COLLECTION), query, field, sort);

		List<ActorInfo> actorInfos = new ArrayList<ActorInfo>();
		for (DBObject log : logList) {
			try {
				actorInfos.add(toActorInfoFromJson(log.toString()));
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}

		return actorInfos;
	}
	
	public static ActorInfo toActorInfoFromJson(String jsonString) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ActorInfo actorInfo = mapper.readValue(jsonString, ActorInfo.class);

		return actorInfo;
	}
}
