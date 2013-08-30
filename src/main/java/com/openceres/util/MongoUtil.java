package com.openceres.util;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;
import com.openceres.config.AsConfiguration;
import com.openceres.exception.DbException;
import com.openceres.property.Const;


/**
 * MongoDB 클라이언트.
 * WriteConcern 자세한 내용은 {@link com.mongodb.WriteConcern} 참고
 *
 * This source is originated from EMR-DM project
 * @author SKCC
 * @category com.sk.services.emr.dm.db
 * @since 2012
 * @version 2.0.0
 *
 */
public class MongoUtil
{
	// Currently all MongoClient logs are written to debug mode
	private final static Logger LOG = Logger.getLogger (MongoUtil.class);

	private Mongo mongo;
	private DB db;

	/**
	 * 디폴트 db로 접속.
	 * 
	 * @throws DbException 서버 접속 실패 
	 * @throws NumberFormatException 잘못된 설정파일 서버정보  
	 */
	public MongoUtil () throws DbException
	{
		this (AsConfiguration.getValue(Const.DB_MONGO_DEFAULT_SERVER), AsConfiguration.getIntValue(Const.DB_MONGO_DEFAULT_PORT), 
				AsConfiguration.getValue(Const.DB_MONGO_DEFAULT_DBNAME), WriteConcern.NONE);
	}
	
	/**
	 * DB를 입력 서버와 포트로 초기화
	 * 
	 * @param server 	몽고디비 서버 
	 * @param port 		몽고디비 포트 
	 * @param dbName 	몽고디비 데이터베이스 이름 
	 * @param wc 		몽고디비 쓰기 방식 
	 * @throws DbException 서버 접속 실패 
	 */
	public MongoUtil(String server, int port, String dbName, WriteConcern wc) throws DbException
	{	
		try
		{
			List<String> serverList = StringUtils.parseString(server, ",");
			if(serverList.size() == 1) {
				this.mongo = new Mongo (new ServerAddress(server, port));
				
				LOG.debug ("Connected to the MongoDB");	
			} else {
				List<ServerAddress> serverAddressList = new ArrayList<ServerAddress>();
				for(int i = 0 ; i < serverList.size(); i++)
				{
					serverAddressList.add(new ServerAddress(serverList.get(i), port));
				}
				this.mongo = new Mongo (serverAddressList);
				
				ReadPreference.secondaryPreferred();
				
				LOG.debug ("Connected to the MongoDB");
			}
		}
		catch (UnknownHostException e)
		{
			throw new DbException ("Unknown Host Exception");
		}

		// 데이터베이스 연결
		db = mongo.getDB (dbName);

		// WriteConcern 설정
		if (wc != null)
		{
			db.setWriteConcern (wc);
		}
		else
		{
			db.setWriteConcern (WriteConcern.NONE);
		}

		// 데이터베이스 컨넥션 구축
		db.requestStart ();
		LOG.debug ("Connected to the MongoDB database " + dbName);		
	}
	
	/**
	 * DB를 입력 서버와 포트로 초기화
	 * 
	 * @param servers 	몽고디비 서버 
	 * @param port 		몽고디비 포트 
	 * @param dbName 	몽고디비 데이터베이스 이름 
	 * @param wc 		몽고디비 쓰기 방식 
	 * @throws DbException 서버 접속 실패 
	 */
	public MongoUtil (List<String> servers, List<Integer> ports, String dbName, WriteConcern wc) throws DbException
	{
		try
		{
			List<ServerAddress> serverAddressList = new ArrayList<ServerAddress>();
			for(int i = 0 ; i < servers.size(); i++)
			{
				serverAddressList.add(new ServerAddress(servers.get(i), ports.get(i)));
			}
			this.mongo = new Mongo (serverAddressList);
			
			ReadPreference.secondaryPreferred();
			
			LOG.debug ("Connected to the MongoDB");
		}
		catch (UnknownHostException e)
		{
			throw new DbException ("Unknown Host Exception");
		}

		// 데이터베이스 연결
		db = mongo.getDB (dbName);

		// WriteConcern 설정
		if (wc != null)
		{
			db.setWriteConcern (wc);
		}
		else
		{
			db.setWriteConcern (WriteConcern.NONE);
		}

		// 데이터베이스 컨넥션 구축
		db.requestStart ();
		LOG.debug ("Connected to the MongoDB database " + dbName);
	}
	
	/**
	 * DB를 replica set seed member 들로 초기화
	 * 
	 * @param servers 	host:port 포멧의 문자열 리스트
	 * @param dbName 	몽고디비 데이터베이스 이름 
	 * @param wc 		몽고디비 쓰기 방식 
	 * @throws DbException 서버 접속 실패 
	 */
	public MongoUtil (ImmutableList<String> servers, String dbName, WriteConcern wc) throws DbException
	{
		ArrayList<ServerAddress> serverAddrs = new ArrayList<ServerAddress> (servers.size ());
		for (String s : servers)
		{
			try
			{
				serverAddrs.add (new ServerAddress (s));
			}
			catch (UnknownHostException e)
			{
				throw new DbException ("Unknown Host: " + s);
			}
		}

		// 데이터베이스 초기화
		this.mongo = new Mongo (serverAddrs);
		LOG.debug ("Connected to the MongoDB: " + serverAddrs);

		this.db = mongo.getDB (dbName);

		// WriteConcern 설정
		if (wc != null)
		{
			db.setWriteConcern (wc);
		}
		else
		{
			db.setWriteConcern (WriteConcern.NONE);
		}

		// 데이터베이스 컨넥션 구축
		db.requestStart ();
		LOG.debug ("Connected to the MongoDB database " + dbName);
	}
	
	/**
	 * DB 객체를 가져온다.
	 * 
	 * @return DB 객체.
	 */
	public DB getDb ()
	{
		return this.db;
	}
	
	/**
	 * 해당 키와 매치 하는 하나의 문서 반환; 고유한 레코드 조회시 사용  
	 *  
	 * @param collection 	검색하고자 하는 컬렉션 
	 * @param query 		검색 조건  
	 * @param fields 		반환되어야할 필드 리스트; null 이면 모든 필드 반환
	 * @return DBObject 요청된 문서. 문서가 없을 경우에는 null 값을 반환한다.
	 */
	public DBObject read (final String collection, final DBObject query, final DBObject fields)
	{
		DBCollection dbCollection = this.db.getCollection (collection);
		return dbCollection.findOne (query, fields);
	}
	
	/**
	 * 해당 키와 매치 되는 모든 문서 반환 
	 * 
	 * @param collection 	검색하고자 하는 컬렉션 
	 * @param query 		검색하고자 하는 데이터 쿼리 
	 * @param fields 		반환되어야할 필드 리스트; null 이면 모든 필드 반환
	 * @return DBObject 요청된 문서. 문서가 없을 경우에는 null 값을 반환한다.
	 */
	public ImmutableList<DBObject> readAll (final String collection, final DBObject query, final DBObject fields)
	{
		DBCollection dbCollection = this.db.getCollection (collection);
		DBCursor cursor = dbCollection.find (query, fields);

		ImmutableList.Builder<DBObject> builder = ImmutableList.builder ();
		while (cursor.hasNext ())
		{
			builder.add (cursor.next ());
		}
		
		return builder.build ();
	}
	
	/**
	 * Get Record Counter 
	 * 
	 * @param collection 	검색하고자 하는 컬렉션 
	 * @param query 		검색하고자 하는 데이터 쿼리 
	 * @return 
	 */
	public long getRecordCount (final String collection, final DBObject query)
	{
		DBCollection dbCollection = this.db.getCollection (collection);
		Long count = dbCollection.count(query);

		return count;
	}	
	
	/**
	 * 해당 키와 매치 되는 모든 문서 반환 
	 * 
	 * @param collection 	검색하고자 하는 컬렉션 
	 * @param query 		검색하고자 하는 데이터 쿼리 
	 * @param fields 		반환되어야할 필드 리스트; null 이면 모든 필드 반환
	 * @param sort 			정렬방식 
	 * @return DBObject 요청된 문서. 문서가 없을 경우에는 null 값을 반환한다.
	 */
	public ImmutableList<DBObject> readAll (final String collection, final DBObject query, final DBObject fields, final DBObject sort)
	{
		DBCollection dbCollection = this.db.getCollection (collection);
		DBCursor cursor = dbCollection.find (query, fields).sort (sort);

		ImmutableList.Builder<DBObject> builder = ImmutableList.builder ();
		while (cursor.hasNext ())
		{
			builder.add (cursor.next ());
		}

		return builder.build ();
	}
	
	/**
	 * 해당 키와 매치 되는 모든 문서 반환 
	 * 
	 * @param collection 	검색하고자 하는 컬렉션 
	 * @param query 		검색하고자 하는 데이터 쿼리 
	 * @param fields 		반환되어야할 필드 리스트; null 이면 모든 필드 반환
	 * @param sort 			정렬방식 
	 * @param limit 			문서수 제한
	 * @return DBObject 요청된 문서. 문서가 없을 경우에는 null 값을 반환한다.
	 */
	public ImmutableList<DBObject> readAll (final String collection, final DBObject query, final DBObject fields, final DBObject sort, final int limit)
	{
		DBCollection dbCollection = this.db.getCollection (collection);
		DBCursor cursor = dbCollection.find (query, fields).sort (sort).limit(limit);

		ImmutableList.Builder<DBObject> builder = ImmutableList.builder ();
		while (cursor.hasNext ())
		{
			builder.add (cursor.next ());
		}

		return builder.build ();
	}
	
	/**
	 * 해당 키와 매치 되는 모든 문서 반환 
	 * 
	 * @param collection 	검색하고자 하는 컬렉션 
	 * @param query 		검색하고자 하는 데이터 쿼리 
	 * @param fields 		반환되어야할 필드 리스트; null 이면 모든 필드 반환
	 * @param sort 			정렬방식 
	 * @param skip
	 * @param limit 			문서수 제한
	 * @return DBObject 요청된 문서. 문서가 없을 경우에는 null 값을 반환한다.
	 */
	public ImmutableList<DBObject> readAll (final String collection, final DBObject query, final DBObject fields, final DBObject sort, 
			final int skip, final int limit)
	{
		DBCollection dbCollection = this.db.getCollection (collection);
		DBCursor cursor = dbCollection.find (query, fields).sort (sort).skip(skip).limit(limit);

		ImmutableList.Builder<DBObject> builder = ImmutableList.builder ();
		while (cursor.hasNext ())
		{
			builder.add (cursor.next ());
		}

		return builder.build ();
	}
	
	/**
	 * 컬렉션에 문서 추가 
	 * 
	 * @param collection 	디비 컬렉션 
	 * @param value 		추가될 문서 
	 * @return 에러코드; 성공시 0
	 */
	public int insert (final String collection, final DBObject value)
	{
		DBCollection dbCollection = this.db.getCollection (collection);
		dbCollection.setWriteConcern (getWriteConcern ());
		// enable update, disable multi-update
		dbCollection.insert (value); 
		
		return handleError (db.getLastError ());
	}
	
	/**
	 * 컬렉션에 문서 추가 
	 * 
	 * @param collection 	디비 컬렉션 
	 * @param jsonString 		추가될 문서 
	 * @return 에러코드; 성공시 0
	 */
	public int insert (final String collection, String jsonString)
	{
		DBCollection dbCollection = this.db.getCollection (collection);
		dbCollection.setWriteConcern (getWriteConcern ());
		DBObject dbObject = (DBObject) JSON.parse(jsonString);
		
		// enable update, disable multi-update
		dbCollection.insert (dbObject); 
		
		return handleError (db.getLastError ());
	}
	
	public int insertMulti(final String collection, List<DBObject> objectList)
	{
		DBCollection dbCollection = this.db.getCollection (collection);
		dbCollection.setWriteConcern (getWriteConcern ());

		// enable update, disable multi-update
		dbCollection.insert (objectList); 
		
		return handleError (db.getLastError ());
	}

	/**
	 * 문서 업데이트
	 *  
	 * @param collection 	업데이트 할 컬렉션 
	 * @param query 		업데이트 할 문서 검색 조건 
	 * @param value 		신규 문서 
	 * @param upsert 		true 면 문서가 없을 때 신규 추가 한다 
	 * @return 에러코드; 성공시 0
	 */
	public int update (final String collection, final DBObject query, final DBObject value, boolean upsert)
	{
		DBCollection dbCollection = this.db.getCollection (collection);
		DBObject r = new BasicDBObject ();
		r.put ("$set", value);

		dbCollection.setWriteConcern (getWriteConcern ());

		// 업데이트 race condition이 생길 경우 findAndModify 고려
		dbCollection.findAndModify (query, null, null, false, r, false, upsert);
		// dbCollection.update(query, r, upsert, false); // disable multi-update

		return handleError (db.getLastError ());
	}
		
	/**
	 * 배열에 추가
	 * 
	 * @param collection 	업데이트 할 컬렉션 
	 * @param query 		업데이트 할 문서 검색 조건 
	 * @param value 		신규 문서 
	 * @return 에러코드; 성공시 0
	 */
	public int addToList (final String collection, final DBObject query, final DBObject value)
	{
		DBCollection dbCollection = this.db.getCollection (collection);
		DBObject r = new BasicDBObject ();
		r.put ("$push", value);

		dbCollection.setWriteConcern (getWriteConcern ());
		dbCollection.update (query, r);
		
		return handleError (db.getLastError ());
	}
	

	/**
	 * 검색된 모든 문서 삭제 
	 * 
	 * @param collection 	업데이트 할 컬렉션 
	 * @param query 		업데이트 할 문서 검색 조건 
	 * @return 에러코드; 성공시 0
	 */
	public int delete (final String collection, final DBObject query)
	{
		DBCollection dbCollection = this.db.getCollection (collection);
		if (getWriteConcern ().equals (WriteConcern.SAFE))
		{
			query.put ("$atomic", true);
		}
		dbCollection.remove (query);
		
		return handleError (db.getLastError ());
	}
	
	/**
	 * 컬렉션을 반환
	 * 
	 * @param collection 검색하는 컬렉션
	 * @return 요청된 컬렉션
	 */
	public DBCollection getCollection (final String collection) 
	{
		return this.db.getCollection (collection);
	}
	
	/**
	 * 컬렉션을 반환하고 해당 컬렉션이 없을 경우 신규 생성
	 * 
	 * @param collection 검색하는 컬렉션
	 * @return 요청된 컬렉션
	 */
	public DBCollection getCollectionOrCreate (final String collection) 
	{
		DBCollection dbCollection = this.db.getCollection (collection);
		if( dbCollection == null) 
		{
			dbCollection = db.createCollection(collection, null);
			
		}
		
		return dbCollection;
	}
	
	
	/**
	 * 몽고디비에서 발생된 에러를 코드로 변환하여 리턴한다 
	 * 
	 * 에러코드 
	 * No Error: 0
	 * Master error code: 10054, 10056, 10058, 10107, 13435, 13436
	 * 
	 * @param errors
	 * @return code 오류 코드, 오류가 없을시 0;
	 */
	private int handleError (DBObject errors)
	{
		/*
		 * 몽고디비 에러 오브젝트의 컨텐츠: ok - true indicates the getLastError command
		 * completed successfully. This does NOT indicate there wasn't a last
		 * error. err - if non-null, indicates an error occurred. Value is a
		 * textual description of the error. code - if set, indicates the error
		 * code which occurred. connectionId - the id of the connection lastOp -
		 * the op-id from the last operation n - if an update was done, this is
		 * the number of documents updated. (업데이트 전용 필드)
		 * 
		 * w 설정시: wnote - if set indicates that something unusual happened that
		 * is related to using w: wtimeout - if timed out, set to the value true
		 * waited - if timed out, how long waited in milliseconds wtime - the
		 * time spent waiting for the operation to complete
		 * 
		 * TODO 현재는 아주 간단한 에러 처리 방식이지만 더 상세한 에러코를 반환하도록 수정
		 */
		Object err = errors.get ("err");
		if (err != null)
		{
			return (Integer) errors.get ("code");
		}
		
		return 0;
	}

	/**
	 * 쓰기 방식 조회 
	 * 
	 * @return WriteConcern 
	 */
	public WriteConcern getWriteConcern ()
	{
		return this.db.getWriteConcern ();
	}
	
	/**
	 * 쓰기 방식 업데이트 
	 * 
	 * @param wc
	 */
	public void setWriteConcern (WriteConcern wc)
	{
		this.db.setWriteConcern (wc);
	}

	/**
	 * 
	 * @param collection 	업데이트 할 컬렉션 
	 * @param indices 		생성할 인덱스들 
	 * @param unique 		고유 인덱스 생성 여부 
	 * @return 에러코드; 성공시 0
	 */
	public int ensureIndex (final String collection, final DBObject indices, final boolean unique)
	{
		DBCollection dbCollection = db.getCollection (collection);		
		dbCollection.ensureIndex (indices, null, unique);
		
		return handleError (db.getLastError ());
	}
	
	/**
	 * 컨넥션 종료 
	 */
	public void close ()
	{
		if (db != null)
		{
			db.requestDone ();
		}
		if (mongo != null)
		{
			mongo.close ();
		}
	}
}

