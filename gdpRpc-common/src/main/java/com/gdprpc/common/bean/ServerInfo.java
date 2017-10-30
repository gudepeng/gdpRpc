package com.gdprpc.common.bean;

import java.io.Serializable;

/**
 * @author 我是金角大王 on 2017-10-27.
 */
public class ServerInfo implements Serializable, Comparable<ServerInfo> {
    /**
     * 负载的值
     */
    private Integer balance;
    /**
     * 服务路径
     */
    private String servicePath;
    /**
     * 服务端ip地址
     */
    private String host="127.0.0.1";
    /**
     * 服务端端口
     */
    private Integer port;

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getServicePath() {
        return servicePath;
    }

    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getZKPath(){
        return this.servicePath+"/"+this.host+":"+this.port;
    }

    @Override
    public int compareTo(ServerInfo o) {
        return this.getBalance().compareTo(o.getBalance());
    }
}
