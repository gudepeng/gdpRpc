package com.gdprpc.sample.client;

import com.gdprpc.common.bean.RpcRequest;
import com.gdprpc.registry.RegistryService;
import com.gdprpc.registry.zookeeper.ZookeeperRegistryService;
import com.gdprpc.rpc.client.AnysClientProxy;
import com.gdprpc.rpc.client.ClientChannelInboundHandler;
import com.gdprpc.rpc.client.GProxy;
import com.gdprpc.rpc.client.RPCFuture;
import com.gdprpc.sample.api.UserServer;
import com.gdprpc.sample.bean.User;

import java.util.UUID;

/**
 * @author 我是金角大王 on 2017-10-29.
 */
public class SampleClient {
    public static void main(String[] args) throws InterruptedException {
        //runSync();
        //runOne();
        runAnsy();
    }

    private static void runAnsy() throws InterruptedException {
        int threadNum = 10;
        final int requestNum = 1000;
        Thread[] threads = new Thread[threadNum];
        RegistryService registryService = new ZookeeperRegistryService();
        registryService.connentToRegistryService("192.168.99.100:32770");
        GProxy gproxy = new GProxy(registryService);
        AnysClientProxy clientHandler = gproxy.creatAnys(UserServer.class);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadNum; ++i) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < requestNum; i++) {
                        String username = "GoldenHornKing" + i;
                        RPCFuture future = clientHandler.call("findUserByUsername",username);
                    }
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        long timeCost = (System.currentTimeMillis() - startTime);
        String msg = String.format("Sync call total-time-cost:%sms, req/s=%s", timeCost, ((double) (requestNum * threadNum)) / timeCost * 1000);
        System.out.println(msg);
    }

    private static void runOne() {
        RegistryService registryService = new ZookeeperRegistryService();
        registryService.connentToRegistryService("192.168.99.100:32770");
        GProxy gproxy = new GProxy(registryService);
        UserServer userServer = gproxy.creat(UserServer.class);
        String username = "GoldenHornKing";
        long startTime = System.currentTimeMillis();
        User user = userServer.findUserByUsername(username);
        System.out.println(user.getUsername() + "    " + user.getPassword() + "    " + user.getLever());
    }

    private static void runSync() throws InterruptedException {
        int threadNum = 10;
        final int requestNum = 1000;
        Thread[] threads = new Thread[threadNum];
        RegistryService registryService = new ZookeeperRegistryService();
        registryService.connentToRegistryService("192.168.99.100:32770");
        GProxy gproxy = new GProxy(registryService);
        UserServer userServer = gproxy.creat(UserServer.class);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadNum; ++i) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < requestNum; i++) {

                        String username = "GoldenHornKing" + i;
                        User user = userServer.findUserByUsername(username);
                        System.out.println("true = " + user.getUsername());
                        if (!user.getUsername().equals("GoldenHornKing" + i)) {
                            System.out.print("error = " + user.getUsername());
                        }
                    }
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        long timeCost = (System.currentTimeMillis() - startTime);
        String msg = String.format("Sync call total-time-cost:%sms, req/s=%s", timeCost, ((double) (requestNum * threadNum)) / timeCost * 10000);
        System.out.println(msg);
    }
}
