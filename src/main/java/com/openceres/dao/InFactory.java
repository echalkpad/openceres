package com.openceres.dao;

import com.openceres.dao.in.FileIn;
import com.openceres.dao.in.In;
import com.openceres.dao.in.MongoIn;

public class InFactory {
	public static In getInstance(String source)
	{
		if(source.equals("MONGO"))
		{
			return MongoIn.getInstance();
		} else if(source.equals("FILE"))
		{
			return FileIn.getInstance();
		}
		else
		{
			return null;
		}
	}
}
