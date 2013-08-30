package com.openceres.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.curator.framework.recipes.cache.ChildData;
import com.netflix.curator.framework.recipes.cache.PathChildrenCache;
import com.openceres.config.AsConfiguration;
import com.openceres.core.common.ActorRole;
import com.openceres.core.common.ResultSet;
import com.openceres.core.executor.listener.AkkaNodeListener;
import com.openceres.exception.NotAvailableActorException;
import com.openceres.model.ActorInfo;
import com.openceres.property.Const;
import com.openceres.util.Parser;
import com.openceres.util.ZkUtil;

public class ActorManager {
	private static final Logger LOG = LoggerFactory.getLogger(ActorManager.class);

	private static ActorManager actorManager = null;

	ZkUtil zkUtil = null;
	final String membership = "akka";

	// Akka actor 관리
	final String proxyPath = "proxy";
	final String masterPath = "master";
	final String workerPath = "worker";
	final String supervisorPath = "supervisor";
	final String nonePath = "none";

	// Actor result 관리
	final String resultPath = "results";

	private ActorManager() {
		// init();
	}

	synchronized public static ActorManager getInstance() {
		if (actorManager == null) {
			actorManager = new ActorManager();
		}

		return actorManager;
	}

	synchronized public void start() {
		if (zkUtil == null)
			init();
	}

	private void init() {
		zkUtil = new ZkUtil(membership).withZkServers(AsConfiguration.getValue(Const.ZK_SERVERS));
		zkUtil.initialize();
		try {
			zkUtil.addPersistent(proxyPath, null);
			zkUtil.addPersistent(masterPath, null);
			zkUtil.addPersistent(workerPath, null);
			zkUtil.addPersistent(supervisorPath, null);
			zkUtil.addPersistent(nonePath, null);

			zkUtil.addPersistent(resultPath, null);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void close() {
		zkUtil.destroy();
	}

	private String getNodeName(String uri, ActorRole role) {
		if (role == ActorRole.MASTER) {
			return masterPath + "/" + convertZooPath(uri);
		} else if (role == ActorRole.PROXY) {
			return proxyPath + "/" + convertZooPath(uri);
		} else if (role == ActorRole.WORKER) {
			return workerPath + "/" + convertZooPath(uri);
		} else if (role == ActorRole.SUPERVISOR) {
			return supervisorPath + "/" + convertZooPath(uri);
		} else {
			return nonePath + "/" + convertZooPath(uri);
		}
	}

	private String getNodeName(ActorRole role) {
		if (role == ActorRole.MASTER) {
			return masterPath;
		} else if (role == ActorRole.PROXY) {
			return proxyPath;
		} else if (role == ActorRole.WORKER) {
			return workerPath;
		} else if (role == ActorRole.SUPERVISOR) {
			return supervisorPath;
		} else {
			return nonePath;
		}
	}

	private String findNode(String uri) {
		try {
			String node = masterPath + "/" + convertZooPath(uri);
			if (zkUtil.isExist(node))
				return node;

			node = proxyPath + "/" + convertZooPath(uri);
			if (zkUtil.isExist(node))
				return node;

			node = workerPath + "/" + convertZooPath(uri);
			if (zkUtil.isExist(node))
				return node;

			node = supervisorPath + "/" + convertZooPath(uri);
			if (zkUtil.isExist(node))
				return node;

			node = nonePath + "/" + convertZooPath(uri);
			if (zkUtil.isExist(node))
				return node;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * 새로운 액터를 추가한다.
	 * 
	 * @param uri
	 * @param actorInfo
	 */
	public synchronized void addActor(String uri, ActorInfo actorInfo) {
		String node = getNodeName(uri, actorInfo.getRole());

		try {
			zkUtil.add(node, actorInfo.toJson().getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void removeActor(String uri, ActorInfo actorInfo) {
		String node = getNodeName(uri, actorInfo.getRole());

		try {
			if (zkUtil.isExist(node)) {
				zkUtil.delete(node);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

	}

	/**
	 * 모든 actor를 삭제한다.
	 * 
	 * @param uri
	 */
	public synchronized void clearAllActors() {
		try {

			zkUtil.deleteAll(this.proxyPath);
			zkUtil.deleteAll(this.masterPath);
			zkUtil.deleteAll(this.workerPath);
			zkUtil.deleteAll(this.supervisorPath);
			zkUtil.deleteAll(this.nonePath);

			// 해당 노드는 삭제 후에도 다시 생성한다.
			zkUtil.addPersistent(proxyPath, null);
			zkUtil.addPersistent(masterPath, null);
			zkUtil.addPersistent(workerPath, null);
			zkUtil.addPersistent(supervisorPath, null);
			zkUtil.addPersistent(nonePath, null);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 운영 중인 액터를 사용가능한 액터로 변경한다.
	 * 
	 * @param uri
	 * @param actorInfo
	 */
	// public synchronized void setAvailable(String uri, String actorInfo)
	// {
	// try{
	// String aNode = this.availablePath + "/" + convertZooPath(uri);
	// String rNode = this.runningPath + "/" + convertZooPath(uri);
	//
	// if(zkUtil.isExist(rNode))
	// {
	// zkUtil.delete(rNode);
	// zkUtil.add(aNode, actorInfo.getBytes());
	// }
	// } catch(Exception e)
	// {
	// LOG.error(e);
	// e.printStackTrace();
	// }
	// }

	/**
	 * 사용 가능 액터를 운영 중 액터로 변경한다.
	 * 
	 * @param uri
	 * @param actorInfo
	 */
	// public synchronized void setUsing(String uri, String actorInfo)
	// {
	// try{
	// String aNode = this.availablePath + "/" + convertZooPath(uri);
	// String rNode = this.runningPath + "/" + convertZooPath(uri);
	//
	// if(zkUtil.isExist(aNode))
	// {
	// zkUtil.delete(aNode);
	// zkUtil.add(rNode, actorInfo.getBytes());
	// }
	// } catch(Exception e)
	// {
	// LOG.error(e);
	// e.printStackTrace();
	// }
	// }

	/**
	 * 사용 가능한 액터를 사용 불 가능한 액터로 변경한다.
	 * 
	 * @param uri
	 * @param actorInfo
	 */
	// public synchronized void setUnreachable(String uri, String actorInfo)
	// {
	// try{
	// String uNode = this.unreachablePath + "/" + convertZooPath(uri);
	// String aNode = this.availablePath + "/" + convertZooPath(uri);
	//
	// if(zkUtil.isExist(aNode))
	// {
	// zkUtil.delete(aNode);
	// zkUtil.add(uNode, actorInfo.getBytes());
	// }
	// } catch(Exception e)
	// {
	// LOG.error(e);
	// e.printStackTrace();
	// }
	// }

	/**
	 * 사용 불가능한 액터를 사용 가능한 액터로 변경한다.
	 * 
	 * @param uri
	 * @param actorInfo
	 */
	// public synchronized void setReachable(String uri, String actorInfo)
	// {
	// try{
	// String uNode = this.unreachablePath + "/" + convertZooPath(uri);
	// String aNode = this.availablePath + "/" + convertZooPath(uri);
	//
	// if(zkUtil.isExist(uNode))
	// {
	// zkUtil.delete(uNode);
	// zkUtil.add(aNode, actorInfo.getBytes());
	// }
	// } catch(Exception e)
	// {
	// LOG.error(e);
	// e.printStackTrace();
	// }
	// }

	/**
	 * 운영 중인 액터의 상태 정보를 업데이트 한다.
	 * 
	 * @param uri
	 * @param actorInfo
	 */
	public synchronized void setData(String uri, ActorInfo actorInfo) {
		String node = this.getNodeName(uri, actorInfo.getRole());

		try {
			if (zkUtil.isExist(node)) {
				zkUtil.setData(node, actorInfo.toJson().getBytes());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 액터의 정보를 가져온다.
	 * 
	 * @param uri
	 * @param role
	 * @return
	 */
	public synchronized ActorInfo getData(String uri, ActorRole role) {
		String node = null;
		ActorInfo actorInfo = null;
		if (role != null) {
			node = this.getNodeName(uri, role);
		} else {
			node = this.findNode(uri);
		}

		if (node == null)
			return null;

		try {
			if (zkUtil.isExist(node)) {
				actorInfo = ActorManager.toActorInfoFromJson(zkUtil.getData(node));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return actorInfo;
	}

	/**
	 * 운영 중인 액터의 상태 정보를 업데이트 한다.
	 * 
	 * @param uri
	 * @param actorInfo
	 */
	// public synchronized void setData(String uri, String actorInfo)
	// {
	// try{
	// String rNode = this.runningPath + "/" + convertZooPath(uri);
	//
	// if(zkUtil.isExist(rNode))
	// {
	// zkUtil.setData(rNode, actorInfo.getBytes());
	// }
	// } catch(Exception e)
	// {
	// LOG.error(e);
	// e.printStackTrace();
	// }
	// }

	// public synchronized ActorInfo getData(String uri)
	// {
	// String rNode = this.availablePath + "/" + convertZooPath(uri);
	// ActorInfo actorInfo = null;
	// try {
	// if(zkUtil.isExist(rNode))
	// {
	// actorInfo = this.toActorInfoFromJson(zkUtil.getData(rNode));
	// }
	// } catch(Exception e)
	// {
	// LOG.error(e);
	// e.printStackTrace();
	// }
	//
	// return actorInfo;
	// }

	public synchronized List<ActorInfo> getDataByRole(ActorRole role) {

		String roleNode = getNodeName(role);
		List<ActorInfo> actorInfoList = new ArrayList<ActorInfo>();
		try {
			if (zkUtil.isExist(roleNode)) {
				List<String> nodeList = zkUtil.getList(roleNode);
				for (String node : nodeList) {
					actorInfoList.add(getData(node, role));
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return actorInfoList;
	}

	public synchronized List<ActorInfo> getAllData() {
		List<ActorInfo> actorInfoList = new ArrayList<ActorInfo>();

		actorInfoList.addAll(getDataByRole(ActorRole.PROXY));
		actorInfoList.addAll(getDataByRole(ActorRole.MASTER));
		actorInfoList.addAll(getDataByRole(ActorRole.SUPERVISOR));
		actorInfoList.addAll(getDataByRole(ActorRole.WORKER));
		actorInfoList.addAll(getDataByRole(ActorRole.NONE));

		return actorInfoList;
	}

	public static String convertZooPath(String uri) {
		return uri.replaceAll("/", "*");
	}

	public static String convertUri(String zooPath) {
		return zooPath.replaceAll("*", "/");
	}

	/**
	 * 사용 가능한 액터를 가져온다. 주의 : 가져온 액터를 가지고 실제 작업을 수행하는 데는 멀티 쓰레드 환경에서 safe 하지 않다.
	 * 
	 * @return
	 * @throws NotAvailableActorException
	 */
	// public synchronized ActorRef getAvailableActor() throws NotAvailableActorException
	// {
	// ActorRef result = null;
	// try{
	// String aNode = this.availablePath;
	// List<String> nodeList = zkUtil.getList(aNode);
	// if(nodeList == null || nodeList.size() == 0) {
	// throw new NotAvailableActorException();
	// }
	// Random rand = new Random();
	// result = actorMap.get(nodeList.get(rand.nextInt(nodeList.size())));
	// } catch(Exception e)
	// {
	// LOG.error(e);
	// e.printStackTrace();
	// }
	//
	// return result;
	// }

	public void deleteCommandResults(String commandId) {
		String nodeName = this.resultPath + "/" + commandId;

		try {
			if (zkUtil.isExist(nodeName)) {
				zkUtil.delete(nodeName);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public ResultSet getCommandResults(String commandId) {
		String nodeName = this.resultPath + "/" + commandId;
		ResultSet resultSet = null;

		try {
			if (zkUtil.isExist(nodeName)) {
				resultSet = (ResultSet) Parser.fromByteArray(zkUtil.getByteData(nodeName));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return resultSet;
	}

	public void setCommandResults(String commandId, boolean isSuccess, Map<String, Object> resultMap) {
		String nodeName = this.resultPath + "/" + commandId;
		ResultSet resultSet = new ResultSet();

		resultSet.isSuccess = isSuccess;
		resultSet.resultMap = resultMap;

		byte[] results = resultSet.toByteArray();

		try {
			if (zkUtil.isExist(nodeName)) {
				zkUtil.setData(nodeName, results);
			} else {
				zkUtil.add(nodeName, results);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public PathChildrenCache createCacheWithListener(ActorRole role, AkkaNodeListener listener)
			throws Exception {
		PathChildrenCache pathChildrenCache = new PathChildrenCache(zkUtil.getClient(), "/akka/"
				+ this.getNodeName(role), true);
		pathChildrenCache.start();
		pathChildrenCache.getListenable().addListener(listener);

		return pathChildrenCache;
	}

	public static List<ActorInfo> getCachedInfo(PathChildrenCache cachedChildren)
			throws IOException {
		List<ChildData> childData = cachedChildren.getCurrentData();

		List<ActorInfo> actorInfoList = new ArrayList<ActorInfo>();
		for (ChildData child : childData) {
			String data = new String(child.getData());
			actorInfoList.add(ActorManager.toActorInfoFromJson(data));
		}

		return actorInfoList;
	}

	public static ActorInfo toActorInfoFromJson(String jsonString) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ActorInfo actorInfo = mapper.readValue(jsonString, ActorInfo.class);

		return actorInfo;
	}
}
