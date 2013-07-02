package com.openceres.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openceres.property.Const;

public class ResourceBundleEx
{
	private static final Logger logger = LoggerFactory.getLogger(ResourceBundleEx.class);
	
	private String[] propFiles = {Const.PROP_FILE_QUARTZ, Const.PROP_FILE_LOGGER};
	
	private Map<String, Properties> propMap  = new HashMap<String, Properties>();
	
	private static ResourceBundleEx resourceBundle;
	
	public static ResourceBundleEx getBundle ()
	{
		if (resourceBundle == null)
		{
			resourceBundle = new ResourceBundleEx ();
		}
		
		return resourceBundle;
	}
	
	public ResourceBundleEx ()
	{
		initialize ();
	}
	
	public boolean initialize ()
	{
		for(String propFile : Arrays.asList(propFiles))
		{
			propMap.put(propFile, loadProperties(propFile));
		}
		
		return true;
	}
	
	private Properties loadProperties(String propFile)
	{
		InputStream in = null;
		
		Properties properties = new Properties ();
		
		try
		{
			// Get input stream from loaded package
			in = this.getClass ().getClassLoader ().getResourceAsStream (propFile);
			
			if (in != null)
			{
				properties.load (in);
				logger.info(propFile + " in classpath is loaded.");
			}
			
			try
			{
				// 파일시스템에 Properties 파일이 있을 경우, Properties 내용을 append.
				in = new FileInputStream (propFile);
				
				if (in != null)
				{
					properties.load (in);
					logger.info(propFile + " in filesystem is appended & loaded.");
				}
			}
			catch (FileNotFoundException e)
			{
				logger.warn(propFile + " in filesystem does not exist. - " + e.getMessage());
			}
		}
		catch (IOException e)
		{
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close ();
				}
			}
			catch (IOException e)
			{
				logger.error(e.getMessage());
				logger.debug(e.toString());
			}
		}
		
		return properties;
	}
	
	public Properties getProperties (String propFile)
	{
		return propMap.get(propFile);
	}
	
	public String getString (String propFile, String key)
	{
		return resourceBundle.getProperties(propFile).getProperty(key);
	}
}
