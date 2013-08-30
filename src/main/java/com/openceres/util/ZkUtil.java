package com.openceres.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.CreateMode;

import com.netflix.curator.RetryPolicy;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheEvent;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheListener;
import com.netflix.curator.retry.RetryNTimes;

public class ZkUtil {
	private static final Logger LOG = LoggerFactory.getLogger(ZkUtil.class);
	
	private CuratorFramework curatorFramework = null;
	
	private String servers;
	private String namespace = "autoscale";
	private String membershipNode;
	
	public static abstract class Listener implements PathChildrenCacheListener
	{
		public abstract void event (PathChildrenCacheEvent event);
		
		public void childEvent (CuratorFramework client, PathChildrenCacheEvent event) throws Exception
		{
			event (event);
		}
	}
	
	public ZkUtil(String membershipNode)
	{
		this.membershipNode = "/" + membershipNode;
	}
	
	public ZkUtil withZkServers(String servers)
	{
		this.servers = servers;
		
		return this;
	}
	
	public boolean initialize()
	{
		boolean result = connect();
		if(result)
		{
			result = setup();
		}
		
		return result;
	}
	
	public void destroy()
	{
		try
		{
			delete("");
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		
		if(this.curatorFramework != null)
		{
			this.curatorFramework.close();
		}
	}
	
	public void addPersistent(String nodeName, byte[] data) throws Exception
	{
		try
		{
			String node = this.membershipNode + "/" + nodeName;
			if(this.curatorFramework.checkExists().forPath(node) == null)
			{
				this.curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(node, data);
			}
			else
			{
				this.curatorFramework.setData().forPath(node, data);
			}
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void add(String nodeName, byte[] data) throws Exception
	{
		try
		{
			String node = this.membershipNode + "/" + nodeName;
			if(this.curatorFramework.checkExists().forPath(node) == null)
			{
				this.curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(node, data);
			}
			else
			{
				this.curatorFramework.setData().forPath(node, data);
			}
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void delete(String nodeName) throws Exception
	{
		try
		{
			String node = "";
			if(nodeName.isEmpty())
			{
				node = this.membershipNode;
			}
			else
			{
				node = this.membershipNode + "/" + nodeName;
			}
			if(this.curatorFramework.checkExists().forPath(node) != null)
			{
				this.curatorFramework.delete().forPath(node);
			}
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void deleteAll(String nodeName) throws Exception
	{
		String node = this.membershipNode + "/" + nodeName;
		LOG.info(node);
		if(this.curatorFramework.checkExists().forPath(node) != null)
		{
			List<String> nodeList = this.getList(nodeName);
			
			if(nodeList == null || nodeList.size() == 0)
			{
				LOG.info("Deleted0 [" + node + "]");
				this.delete(nodeName);
			}
			else {
				for(String lastNode : nodeList)
				{
					LOG.info("Deleted1 [" + node + "]");
					deleteAll(nodeName + "/" + lastNode);
				}
				LOG.info("Deleted2 [" + node + "]");
				this.delete(nodeName);
			}
		}
	}
	
	public void setData(String nodeName, byte[] data) throws Exception
	{
		try
		{
			String node = this.membershipNode + "/" + nodeName;
			if(this.curatorFramework.checkExists().forPath(node) != null)
			{
				this.curatorFramework.setData().forPath(node, data);
			}
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
	}
	
	public byte[] getByteData(String nodeName) throws Exception
	{
		byte[] data = null;
		try
		{
			String node = this.membershipNode + "/" + nodeName;
			if(this.curatorFramework.checkExists().forPath(node) != null)
			{
			
				data = this.curatorFramework.getData().forPath(node);
			}
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		
		return data;
	}
	
	public String getData(String nodeName) throws Exception
	{
		String data = null;
		try
		{
			String node = this.membershipNode + "/" + nodeName;
			if(this.curatorFramework.checkExists().forPath(node) != null)
			{
			
				data = new String(this.curatorFramework.getData().forPath(node));
			}
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		
		return data;
	}
	
	public List<String> getList(String nodeName) throws Exception
	{
		List<String> result = null;
		
		try
		{
			String node = "";
			if(nodeName.isEmpty())
			{
				node = this.membershipNode;
			}
			else
			{
				node = this.membershipNode + "/" + nodeName;
			}
			
			result = this.curatorFramework.getChildren().forPath(node);
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		
		return result;
	}
	
	public boolean isExist(String nodeName) throws Exception
	{
		boolean result = false;
		
		try
		{
			String node = this.membershipNode + "/" + nodeName;
			if(this.curatorFramework.checkExists().forPath(node) != null)
			{
				result = true;
			}
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		
		return result;
	}
	
	private boolean connect()
	{
		boolean result = false;
		
		try 
		{
			RetryPolicy retryPolicy = new RetryNTimes (3600, 10000);
			this.curatorFramework = CuratorFrameworkFactory.newClient(this.servers, retryPolicy);
			
			this.curatorFramework = CuratorFrameworkFactory.builder()
										.connectString(this.servers)
										.namespace(this.namespace)
										.connectionTimeoutMs(5000)
										.sessionTimeoutMs(10000)
										.retryPolicy(retryPolicy)
										.build();
			
			this.curatorFramework.start();
			
			result = true;
				
		} catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		
		return result;
	}
	
	private boolean setup()
	{
		boolean result = false;
		
		try
		{
			if(this.curatorFramework.checkExists().forPath(this.membershipNode) == null)
			{
				this.curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(this.membershipNode);
			}
			
			result = true;
		}
		catch (Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		
		return result;
	}
	
	public CuratorFramework getClient()
	{
		return this.curatorFramework;
	}
}
