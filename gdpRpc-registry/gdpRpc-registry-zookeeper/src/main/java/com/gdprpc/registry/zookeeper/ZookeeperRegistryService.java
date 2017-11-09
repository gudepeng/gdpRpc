package com.gdprpc.registry.zookeeper;

import com.gdprpc.common.bean.ServerInfo;
import com.gdprpc.common.util.SerializationUtil;
import com.gdprpc.registry.RegistryService;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 我是金角大王 on 2017-10-23.
 */
public class ZookeeperRegistryService implements RegistryService {

    private static final String ZKPARENTPATH = "/gdprpc/";
    private CuratorFramework client;
    /**
     * 服务监听集合
     */
    private ConcurrentHashMap<String, PathChildrenCache> pathChildrenCacheMap = new ConcurrentHashMap<>();
    /**
     * 服务集合
     */
    private ConcurrentHashMap<String, List<ServerInfo>> serverInfoMap = new ConcurrentHashMap<>();

    @Override
    public void connentToRegistryService(String zookeeperPath) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(zookeeperPath, retryPolicy);
        client.start();
    }

    @Override
    public void register(ServerInfo serverInfo) {
        try {
            if (client.checkExists().forPath(ZKPARENTPATH + serverInfo.getZKPath()) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(ZKPARENTPATH + serverInfo.getZKPath(), SerializationUtil.serialize(serverInfo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public ServerInfo discover(final String serverName) throws InterruptedException {
        List<ServerInfo> listServverInfo = serverInfoMap.get(serverName);
        if(listServverInfo == null){
            PathChildrenCache pathChildrenCache = pathChildrenCacheMap.get(serverName);
            if (pathChildrenCache == null) {
                try {
                    listServverInfo = new ArrayList<>();
                    List<String> strings = client.getChildren().forPath(ZKPARENTPATH + serverName);
                    for (String child : strings) {
                        listServverInfo.add(getDate(serverName + "/" + child));
                    }
                    serverInfoMap.put(serverName,listServverInfo);

                    PathChildrenCache newPathChildrenCache = new PathChildrenCache(client, ZKPARENTPATH + serverName, true);
                    pathChildrenCache = pathChildrenCacheMap.putIfAbsent(serverName, newPathChildrenCache);
                    if (pathChildrenCache == null) {
                        pathChildrenCache = newPathChildrenCache;
                        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                            @Override
                            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                                switch (event.getType()) {
                                    case CHILD_ADDED: {
                                        if (serverInfoMap.get(serverName) == null) {
                                            ServerInfo serverInfo = SerializationUtil.deserialize(event.getData().getData(), ServerInfo.class);
                                            List<ServerInfo> list = new ArrayList<>();
                                            list.add(serverInfo);
                                            serverInfoMap.put(serverName, list);
                                        } else {
                                            ServerInfo serverInfo = SerializationUtil.deserialize(event.getData().getData(), ServerInfo.class);
                                            List<ServerInfo> list = serverInfoMap.get(serverName);
                                            list.add(serverInfo);
                                        }
                                        break;
                                    }
                                    case CHILD_REMOVED: {
                                        ServerInfo serverInfo = SerializationUtil.deserialize(event.getData().getData(), ServerInfo.class);
                                        List<ServerInfo> list = serverInfoMap.get(serverName);
                                        for (ServerInfo si : list) {
                                            if (si.equals(serverInfo)) {
                                                list.remove(si);
                                            }
                                        }
                                        break;
                                    }
                                    case CHILD_UPDATED: {
                                        ServerInfo serverInfo = SerializationUtil.deserialize(event.getData().getData(), ServerInfo.class);
                                        List<ServerInfo> list = serverInfoMap.get(serverName);
                                        for (int i = 0; i < list.size(); i++) {
                                            if (list.get(i).equalsOther(serverInfo)) {
                                                list.set(i, serverInfo);
                                            }
                                        }
                                        break;
                                    }
                                    default: {
                                        break;
                                    }
                                }
                            }
                        });
                        newPathChildrenCache.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (listServverInfo!=null&&listServverInfo.size() > 0) {
            Collections.sort(listServverInfo);
            return listServverInfo.get(0);
        } else {
            return null;
        }
    }

    public ServerInfo discoverNocache(String serverName) {
        List<ServerInfo> serverNode = null;
        try {
            serverNode = new ArrayList<ServerInfo>();
            List<String> strings = client.getChildren().forPath(ZKPARENTPATH + serverName);
            for (String child : strings) {
                serverNode.add(getDate(serverName + "/" + child));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (serverNode.size() > 0) {
            Collections.sort(serverNode);
            return serverNode.get(0);
        } else {
            return null;
        }
    }

    @Override
    public ServerInfo getDate(String zookNode) {
        byte[] addressNodes = new byte[0];
        try {
            addressNodes = client.getData().forPath(ZKPARENTPATH + zookNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SerializationUtil.deserialize(addressNodes, ServerInfo.class);
    }

    @Override
    public void setDate(String zookNode, ServerInfo serverInfo) {
        try {
            client.setData().forPath(ZKPARENTPATH + zookNode, SerializationUtil.serialize(serverInfo));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public void destroy() {
        client.close();
    }
}
