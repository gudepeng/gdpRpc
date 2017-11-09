package com.gdprpc.registry;

import com.gdprpc.common.bean.ServerInfo;

/**
 * @author 我是金角大王 on 2017-10-25.
 */
public interface RegistryService {
    /***
     * 链接到zookeeper服务器
     */
    void connentToRegistryService(String zookeeperPath);

    /***
     * 断开zookeeper服务器链接
     */
    void destroy();

    /***
     * 注册服务
     *
     * @param serverName 服务名称
     */
    void register(ServerInfo serverInfo);

    /***
     * 获取服务信息
     *
     * @param serverName 服务名称
     * @return 服务信息
     */
    ServerInfo discover(String serverName) throws InterruptedException;

    /***
     * 获取节点数据
     *
     * @param zookNode 节点路径
     */
    ServerInfo getDate(String zookNode);

    /***
     * 设置节点数据
     *
     * @param zookNode 节点路径
     * @param  serverinfo 设置的值
     */
    void setDate(String zookNode,ServerInfo serverinfo);
}
