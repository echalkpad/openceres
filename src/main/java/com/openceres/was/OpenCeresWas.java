package com.openceres.was;

import java.io.File;
import java.net.URL;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openceres.config.AsConfiguration;
import com.openceres.property.Const;

/**
 * 
 * <h1>AutoscaleWas</h1>
 * 
 * (Quartz Scheduler - AKKA - JMS) job Monitoring System 
 *
 * @author changbaechoi
 *
 */
public class OpenCeresWas 
{
	private static final Logger LOG = LoggerFactory.getLogger(OpenCeresWas.class);
	
	/**
	 * HTTP Service port
	 */
	private int HTTP_PORT = -1;
	
	/**
	 * HTTP Service address
	 */
	private final String HTTP_HOST = "0.0.0.0";
	
	/**
	 * Jetty Server Object
	 */
	private Server server;
	
	/**
	 * Default constructor
	 */
	public OpenCeresWas()
	{
		initialize();
	}
	
	public boolean initialize()
	{
		this.HTTP_PORT = AsConfiguration.getIntValue(Const.MON_PORT);
		
		this.server = new Server();
		
		Connector connector = new SelectChannelConnector();
		connector.setPort(this.HTTP_PORT);
		connector.setHost(this.HTTP_HOST);
		
		this.server.addConnector(connector);
		
		return true;
	}
	
	public void start() throws Exception
	{
		if(this.server == null)
		{
			LOG.error("Http server is not running...");
			return;
		}
		
		URL warUrl = Thread.currentThread().getContextClassLoader().getResource("webapp");
		String warUrlString = warUrl.toExternalForm();
		WebAppContext context = new WebAppContext(warUrlString, "/");
		context.setTempDirectory(new File("./temp"));
		
		context.addServlet(new ServletHolder (new OpenCeresServlet()), "/servlets/OpenCeresServlet");
		
		this.server.setHandler(context);
		
		this.server.start();
		this.server.setStopAtShutdown(true);
		
		LOG.info("START: Web Application for OpenCeres Manager.");
	}
	
	public void stop() throws Exception
	{
		if(this.server == null)
		{
			return;
		}
		
		this.server.stop();
		
		LOG.info("STOP : Web Application for OpenCeres Manager.");
	}
	
	public static void main (String[] args)
	{
		OpenCeresWas was = new OpenCeresWas();
		
		try 
		{
			was.start();
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
	}
}
