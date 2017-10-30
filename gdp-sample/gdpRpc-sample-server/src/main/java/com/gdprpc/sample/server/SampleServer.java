package com.gdprpc.sample.server;

import com.gdprpc.registry.RegistryService;
import com.gdprpc.registry.zookeeper.ZookeeperRegistryService;
import com.gdprpc.rpc.server.DefaultServer;
import com.gdprpc.rpc.server.GServer;
import com.gdprpc.sample.api.UserServer;
import com.gdprpc.sample.server.api.impl.UserServerImpl;

/**
 * @author 我是金角大王 on 2017-10-29.
 */
public class SampleServer {
    public static void main(String[] args){
        RegistryService registryService = new ZookeeperRegistryService();
        registryService.connentToRegistryService("192.168.99.100:32770");
        UserServer userServer = new UserServerImpl();
        GServer gServer = new DefaultServer(8099,registryService).provider(userServer).register().start();
    }
}
