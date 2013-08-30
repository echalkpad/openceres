package com.openceres.dao;

import com.openceres.dao.out.FileOut;
import com.openceres.dao.out.MongoOut;
import com.openceres.dao.out.Out;

public class OutFactory {
	public static Out getInstance(String source)
	{
		if(source.equals("MONGO"))
		{
			return MongoOut.getInstance();
		} else if(source.equals("FILE"))
		{
			return FileOut.getInstance();
		} 
		else {
			return null;
		}
	}
}
