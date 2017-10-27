package com.gdprpc.common.bean;

import java.io.Serializable;

/**
 * Created by 我是金角大王 on 2017-10-27.
 */
public class ServerInfo implements Serializable, Comparable<ServerInfo> {
    /**
     * 负载的值
     */
    private Integer balance;
    /**
     * 服务端ip地址
     */
    private String host;
    /**
     * 服务端端口
     */
    private Integer port;

    public ServerInfo() {
    }

    public ServerInfo(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
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

    public int compareTo(ServerInfo o) {
        return this.getBalance().compareTo(o.getBalance());
    }
}
