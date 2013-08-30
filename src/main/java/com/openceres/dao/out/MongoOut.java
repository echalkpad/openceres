package com.openceres.dao.out;

import com.openceres.config.AsConfiguration;
import com.openceres.exception.DbException;
import com.openceres.model.ActorInfo;
import com.openceres.property.Const;
import com.openceres.util.MongoUtil;

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

	public void writeLog(ActorInfo actorInfo) {
		mongoUtil.insert(AsConfiguration.getValue(Const.DB_MONGO_LOG_COLLECTION), actorInfo.toJson());
	}
}
