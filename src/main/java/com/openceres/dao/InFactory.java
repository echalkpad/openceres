package com.openceres.dao;

import com.openceres.dao.in.In;
import com.openceres.dao.in.MongoIn;

public class InFactory {
	public static In getInstance(String source)
	{
		if(source.equals("MONGO"))
		{
			return MongoIn.getInstance();
		} else
		{
			return null;
		}
	}
}
