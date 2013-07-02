package com.openceres.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.activemq.util.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openceres.core.executor.TaskSingleJobActor;

public class Parser {
	private static Logger LOG = LoggerFactory.getLogger(TaskSingleJobActor.class);

	public static byte[] toByteArray(Object obj)
	{
		byte[] bytes = null;
		
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		
		try 
		{
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			
			oos.writeObject(obj);
			oos.flush();
			
			bytes = bos.toByteArray();
		}
		catch(IOException e)
		{
			LOG.error(e.getMessage(), e);
		}
		finally
		{
			if(oos != null)
			{
				try { oos.close(); } catch (IOException e){;};
			}
			if(bos != null)
			{
				try {bos.close(); } catch (IOException e){;};
			}
		}
		
		return bytes;
	}
	
	public static Object fromByteArray(byte[] bytes)
	{
		Object obj = null;
		
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		
		try
		{
			bis = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bis);
			
			obj = ois.readObject();
		}
		catch (IOException e)
		{
			LOG.error(e.getMessage(), e);
		}
		catch (ClassNotFoundException e)
		{
			LOG.error(e.getMessage(), e);
		}
		finally
		{
			if(ois != null)
			{
				try {ois.close(); } catch (IOException e){;};
			}
			if(bis != null)
			{
				try {bis.close(); } catch (IOException e){;};
			}
		}
		return obj;
	}
}
