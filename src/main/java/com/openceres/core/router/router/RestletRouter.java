package com.openceres.core.router.router;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openceres.config.AsConfiguration;
import com.openceres.core.scheduler.QuartzScheduler;


public class RestletRouter extends RouteBuilder{
	private static final Logger LOG = LoggerFactory.getLogger(RestletRouter.class);

	@Override
	public void configure() throws Exception {
		
		String port = AsConfiguration.getInstance().get("as.server.port");
		
		// Common POST method
		from("restlet:http://localhost:" + port + "/openceres?restletMethod=post")
		.process(new Processor() {
			
			@Override
            public void process(Exchange exchange) throws Exception {
				Map<String, Object>headersMap = exchange.getIn().getHeaders();
				Iterator<String> headersKeyIter = headersMap.keySet().iterator();
				while(headersKeyIter.hasNext())
				{
					String key = headersKeyIter.next();
					LOG.debug( "POST " + key + ":" + headersMap.get(key));
				}
				
				
				String jsonString = exchange.getIn().getBody(String.class);
				LOG.debug(jsonString);
				
            }
        });
		
		// Common GET method
		from("restlet:http://localhost:" + port + "/openceres?restletMethod=get")
		.process(new Processor(){

			@Override
			public void process(Exchange exchange) throws Exception {
				Map<String, Object>headersMap = exchange.getIn().getHeaders();
				Iterator<String> headersKeyIter = headersMap.keySet().iterator();
				while(headersKeyIter.hasNext())
				{
					String key = headersKeyIter.next();
					LOG.debug( "GET " + key + ":" + headersMap.get(key));
				}
				
				exchange.getOut().setBody("Success");
			}
		});
		
		/**
		 * POST: 스케쥴 정보를 등록한다.
		 */
		from("restlet:http://localhost:" + port + "/schedule/add/{group}/{name}/{interval}/{starttime}/{endtime}/{repeat}/{classname}?restletMethod=post")
		.process(new Processor() {
			
			@Override
            public void process(Exchange exchange) throws Exception {
				Map<String, Object>headersMap = exchange.getIn().getHeaders();
				Iterator<String> headersKeyIter = headersMap.keySet().iterator();
				while(headersKeyIter.hasNext())
				{
					String key = headersKeyIter.next();
					LOG.debug( "GET " + key + ":" + headersMap.get(key));
				}
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				String group = (String)exchange.getIn().getHeader("group");
				String name = (String)exchange.getIn().getHeader("name");
				int interval = Integer.parseInt((String)exchange.getIn().getHeader("interval"));
				Date startTime =  sdf.parse((String)exchange.getIn().getHeader("starttime"));
				Date endTime =  sdf.parse((String)exchange.getIn().getHeader("endtime"));
				int repeat = Integer.parseInt((String)exchange.getIn().getHeader("repeat"));
				String jobClass = (String)exchange.getIn().getHeader("classname");
		
				QuartzScheduler.getInstance().addSchedule(group, name, group, name, interval, startTime, endTime, repeat, Class.forName(jobClass));
				
				LOG.info("Shedule is added [" + group + "." + name + "]");
				
				exchange.getOut().setBody("SUCCESS");
            }
        });
		
		/**
		 * POST: 스케쥴 정보를 삭제한다.
		 */
		from("restlet:http://localhost:" + port + "/schedule/delete/{group}/{name}?restletMethod=post")
		.process(new Processor() {
			
			@Override
            public void process(Exchange exchange) throws Exception {
				Map<String, Object>headersMap = exchange.getIn().getHeaders();
				Iterator<String> headersKeyIter = headersMap.keySet().iterator();
				while(headersKeyIter.hasNext())
				{
					String key = headersKeyIter.next();
					LOG.debug( "GET " + key + ":" + headersMap.get(key));
				}
				
				String group = (String)exchange.getIn().getHeader("group");
				String name = (String)exchange.getIn().getHeader("name");
				
				QuartzScheduler.getInstance().deleteSchedule(group, name);
				
				LOG.info("Shedule is deleted [" + group + "." + name + "]");
				
				exchange.getOut().setBody("SUCCESS");
            }
        });
		
		/**
		 * POST: 스케쥴 정보를 변경한다.
		 */
		from("restlet:http://localhost:" + port + "/schedule/update/{group}/{name}/{interval}/{starttime}/{endtime}/{repeat}?restletMethod=post")
		.process(new Processor() {
			
			@Override
            public void process(Exchange exchange) throws Exception{
				Map<String, Object>headersMap = exchange.getIn().getHeaders();
				Iterator<String> headersKeyIter = headersMap.keySet().iterator();
				while(headersKeyIter.hasNext())
				{
					String key = headersKeyIter.next();
					LOG.debug( "GET " + key + ":" + headersMap.get(key));
				}
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				String group = (String)exchange.getIn().getHeader("group");
				String name = (String)exchange.getIn().getHeader("name");
				int interval = Integer.parseInt((String)exchange.getIn().getHeader("interval"));
				Date startTime =  sdf.parse((String)exchange.getIn().getHeader("starttime"));
				Date endTime =  sdf.parse((String)exchange.getIn().getHeader("endtime"));
				int repeat = Integer.parseInt((String)exchange.getIn().getHeader("repeat"));
		
				QuartzScheduler.getInstance().updateSchedule(group, name, interval, startTime, endTime, repeat);
				
				exchange.getOut().setBody("SUCCESS");
            }
        });
	}
}
