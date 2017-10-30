package com.gdprpc.registry.zookeeper.balanceupdateprovider;

import com.gdprpc.common.bean.ServerInfo;
import com.gdprpc.registry.RegistryService;

/**
 * @author 我是金角大王 on 2017-10-27.
 */
public class DefaultBalanceUpdateProvider implements BalanceUpdateProvider{
    private RegistryService registryservice;
    private String zookNode;

    public DefaultBalanceUpdateProvider(RegistryService registryservice, String zookNode) {
        super();
        this.registryservice = registryservice;
        this.zookNode = zookNode;
    }

    @Override
    public boolean addBalance(Integer step) {
        return upBalance(step);
    }

    @Override
    public boolean reduceBalance(Integer step) {
        return upBalance(-step);
    }

    public boolean upBalance(Integer step){
        try {
            ServerInfo serverInfo = registryservice.getDate(zookNode);
            serverInfo.setBalance(serverInfo.getBalance()+step);
            registryservice.setDate(zookNode,serverInfo);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
