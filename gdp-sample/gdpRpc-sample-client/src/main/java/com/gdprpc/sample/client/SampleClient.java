package com.gdprpc.sample.client;

import com.gdprpc.registry.RegistryService;
import com.gdprpc.registry.zookeeper.ZookeeperRegistryService;
import com.gdprpc.rpc.client.GProxy;
import com.gdprpc.sample.api.UserServer;
import com.gdprpc.sample.bean.User;

/**
 * @author 我是金角大王 on 2017-10-29.
 */
public class SampleClient {
    public static void main(String[] args) {
        RegistryService registryService = new ZookeeperRegistryService();
        registryService.connentToRegistryService("192.168.99.100:32770");
        GProxy gproxy = new GProxy(registryService);
        UserServer userServer = gproxy.creat(UserServer.class);
        String username = "GoldenHornKing";
        User user = userServer.findUserByUsername(username);
        System.out.println(user.getUsername() + "    " + user.getPassword() + "    " + user.getLever());
    }
}
