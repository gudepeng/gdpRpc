package com.gdprpc.common.bean;

import java.io.Serializable;
import java.util.List;

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
    private List<String> servicePath;
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

    public List<String> getServicePath() {
        return servicePath;
    }

    public void setServicePath(List<String> servicePath) {
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
        return "/"+this.host+":"+this.port;
    }

    @Override
    public int compareTo(ServerInfo o) {
        return this.getBalance().compareTo(o.getBalance());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){return true;}
        if (o == null || getClass() != o.getClass()){return false;}

        ServerInfo that = (ServerInfo) o;

        if (balance != null ? !balance.equals(that.balance) : that.balance != null){return false;}
        if (servicePath != null ? !servicePath.equals(that.servicePath) : that.servicePath != null){return false;}
        if (host != null ? !host.equals(that.host) : that.host != null){return false;}
        return port != null ? port.equals(that.port) : that.port == null;

    }
    public boolean equalsOther(Object o) {
        if (this == o){return true;}
        if (o == null || getClass() != o.getClass()){return false;}

        ServerInfo that = (ServerInfo) o;

        if (servicePath != null ? !servicePath.equals(that.servicePath) : that.servicePath != null){return false;}
        if (host != null ? !host.equals(that.host) : that.host != null){return false;}
        return port != null ? port.equals(that.port) : that.port == null;

    }
    @Override
    public int hashCode() {
        int result = balance != null ? balance.hashCode() : 0;
        result = 31 * result + (servicePath != null ? servicePath.hashCode() : 0);
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        return result;
    }
}
