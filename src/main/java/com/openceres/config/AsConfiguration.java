package com.openceres.config;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.recipes.cache.PathChildrenCache;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheEvent;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheListener;
import com.openceres.util.ZkUtil;

public class AsConfiguration extends Configuration<AsConfiguration> {
	private static Logger LOG = LoggerFactory.getLogger(AsConfiguration.class);

	static {
		addDefaultResource("as-global.xml");
		addDefaultResource("as-site.xml");
		addDefaultResource("conf/as-global.xml");
		addDefaultResource("conf/as-site.xml");
	}

	final String membership = "properties";

	private ZkUtil zkUtil = null;
	PathChildrenCache pathChildrenCache = null;

	private static AsConfiguration asConf;

	public static AsConfiguration getInstance() {
		if (asConf == null) {
			asConf = new AsConfiguration();
		}

		return asConf;
	}

	AsConfiguration() {
		super();
	}

	protected void init() {
		String zkServers = get("zk.servers");
		zkUtil = new ZkUtil(membership).withZkServers(zkServers);
		zkUtil.initialize();

		// Start to monitor about properties.
		try {
			pathChildrenCache = new PathChildrenCache(zkUtil.getClient(), membership, true);
			pathChildrenCache.start();
			pathChildrenCache.getListenable().addListener(new PropertiesListener());

		} catch (Exception e) {
			LOG.error("Properties Listener add failed...: ", e);
		}
	}

	public void shutdown() {
		zkUtil.destroy();
	}

	public void startup() {
		init();
	}

	public void upload() throws Exception {
		Iterator<Entry<String, String>> iter = this.iterator();

		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			String key = entry.getKey();
			String value = entry.getValue();

			if (key.startsWith("global")) {
				zkUtil.addPersistent(key, value.getBytes());
			}
		}
	}

	public void download() throws Exception {
		List<String> keys = zkUtil.getList("");

		for (String key : keys) {
			String val = zkUtil.getData(key);
			set(key, val);
		}
	}

	public class PropertiesListener implements PathChildrenCacheListener {
		@Override
		public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
				throws Exception {

			switch (event.getType()) {
			case CHILD_ADDED: {
				LOG.info("Node added: " + event.getData().getPath() + ":" + get(event.getData().getPath()));

				set(event.getData().getPath(), new String(event.getData().getData()));
				break;
			}

			case CHILD_UPDATED: {
				LOG.info("Node changed: " + event.getData().getPath() + ":" + get(event.getData().getPath()));

				set(event.getData().getPath(), new String(event.getData().getData()));
				break;
			}

			case CHILD_REMOVED: {
				LOG.info("Node removed: " + event.getData().getPath() + ":" + get(event.getData().getPath()));
				break;
			}

			default: {
				LOG.warn("Unmatched event : " + event.getType().name());
				break;
			}
			}
		}
	}
	
	public static String getValue(String key)
	{
		return AsConfiguration.getInstance().get(key);
	}
	
	public static int getIntValue(String key)
	{
		return AsConfiguration.getInstance().getInt(key, 0);
	}
	
	public static int getIntValue(String key, int defaultValue)
	{
		return AsConfiguration.getInstance().getInt(key, defaultValue);
	}

}
