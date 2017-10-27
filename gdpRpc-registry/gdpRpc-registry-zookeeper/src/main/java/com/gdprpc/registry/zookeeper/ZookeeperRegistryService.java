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
 * Created by 我是金角大王 on 2017-10-23.
 */
public class ZookeeperRegistryService implements RegistryService {

    private final static String ZKPARENTPATH = "/gdprpc/";
    private CuratorFramework client;
    private String addressNode = "127.0.0.1:8099";

    @Override
    public void connentToRegistryService() {
        String zookeeperConnectionString = "192.168.99.100:32773";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();
    }

    @Override
    public void register(String serverName) {
        try {
            if (client.checkExists().forPath(ZKPARENTPATH + serverName + "/address") == null) {
                ServerInfo serverInfo = new ServerInfo();
                serverInfo.setHost("127.0.0.1");
                serverInfo.setPort(8099);
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(ZKPARENTPATH + serverName + "/127.0.0.1:8099", SerializationUtil.serialize(serverInfo));
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
                byte[]  addressNodes = client.getData().forPath(ZKPARENTPATH + serverName + "/"+child);
                ServerInfo serverinfo = SerializationUtil.deserialize(addressNodes,ServerInfo.class);
                serverNode.add(serverinfo);
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
    public void destroy() {
        client.close();
    }
}
