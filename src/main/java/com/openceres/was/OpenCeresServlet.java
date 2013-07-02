package com.openceres.was;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenCeresServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(OpenCeresServlet.class);
	
	public OpenCeresServlet()
	{
		super();

	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		PrintWriter out = null;
		
		response.setContentType("text/html");
		
		String content = "";
		
		try 
		{
			out = response.getWriter();
					
			//TODO 명령에 따라 해야할일 정의 
			
			content = "Success! Please wait...";
		}
		catch (Exception e)
		{
			content = e.toString();
			LOG.error(e.getMessage(), e);
		}
		finally
		{
			out.println(content);
			out.flush();
		}
		
	}
	
}
