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
import java.util.Map;
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
    private ConcurrentHashMap<String, List<String>> serverInfoMap = new ConcurrentHashMap<>();

    @Override
    public void connentToRegistryService(String zookeeperPath) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(zookeeperPath, retryPolicy);
        client.start();
    }

    @Override
    public void register(ServerInfo serverInfo) {
        try {
            for(String path:serverInfo.getServicePath()){
                if (client.checkExists().forPath(ZKPARENTPATH + path+serverInfo.getZKPath()) == null) {
                    client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(ZKPARENTPATH + path+serverInfo.getZKPath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Map<String,Object> discover(final String serverName) throws InterruptedException {
        List<String> listServverInfo = serverInfoMap.get(serverName);
        if(listServverInfo == null){
            PathChildrenCache pathChildrenCache = pathChildrenCacheMap.get(serverName);
            if (pathChildrenCache == null) {
                try {
                    listServverInfo = client.getChildren().forPath(ZKPARENTPATH + serverName);
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
                                            List<String> list = new ArrayList<>();
                                            list.add(event.getData().getPath());
                                            serverInfoMap.put(serverName, list);
                                        } else {
                                            List<String> list = serverInfoMap.get(serverName);
                                            list.add(event.getData().getPath());
                                        }
                                        break;
                                    }
                                    case CHILD_REMOVED: {
                                        List<String> list = serverInfoMap.get(serverName);
                                        for (String si : list) {
                                            if (si.equals(event.getData().getPath())) {
                                                list.remove(si);
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
            Map<String,Object> map = new ConcurrentHashMap<>();
            map.put("host",listServverInfo.get(0).split(":")[0]);
            map.put("prot",listServverInfo.get(0).split(":")[1]);
            return map;
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
