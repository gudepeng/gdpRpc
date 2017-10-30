package com.gdprpc.registry.zookeeper;

import com.gdprpc.common.bean.ServerInfo;
import com.gdprpc.common.util.SerializationUtil;
import com.gdprpc.registry.RegistryService;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 我是金角大王 on 2017-10-23.
 */
public class ZookeeperRegistryService implements RegistryService {

    private final static String ZKPARENTPATH = "/gdprpc/";
    private CuratorFramework client;

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
    public ServerInfo discover(String serverName) {
        List<ServerInfo> serverNode = null;
        try {
            serverNode = new ArrayList<ServerInfo>();
            List<String> strings = client.getChildren().forPath(ZKPARENTPATH + serverName);
            for(String child:strings){
                serverNode.add(getDate(serverName + "/"+child));
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
        byte[]  addressNodes = new byte[0];
        try {
            System.out.println(ZKPARENTPATH+zookNode);
            addressNodes = client.getData().forPath(ZKPARENTPATH+zookNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SerializationUtil.deserialize(addressNodes,ServerInfo.class);
    }

    @Override
    public void setDate(String zookNode, ServerInfo serverInfo) {
        try {
            client.setData().forPath(ZKPARENTPATH+zookNode,SerializationUtil.serialize(serverInfo));
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
