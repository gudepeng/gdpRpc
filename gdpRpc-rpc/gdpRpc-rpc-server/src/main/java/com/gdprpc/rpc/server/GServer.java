package com.gdprpc.rpc.server;

/**
 * @author 我是金角大王 on 2017-10-30.
 */
public interface GServer {
    /**
     * 设置需要提供服务的类
     * @return 返回服务端类
     * */
    GServer provider(Object serviceProvider);

    /**
     * 注册到本地服务中
     * @return 返回服务端类
     * */
    GServer register();

    /**
     * 启动netty服务并把服务到注册中心中
     * @return 返回服务端类
     * */
    DefaultServer start();
}
