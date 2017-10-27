package com.gdprpc.rpc.client;

import com.gdprpc.common.bean.RpcRequest;
import com.gdprpc.common.bean.RpcResponse;
import com.gdprpc.common.bean.ServerInfo;
import com.gdprpc.registry.zookeeper.ZookeeperRegistryService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by 我是金角大王 on 2017-10-25.
 */
public class GProxy {

    public <T> T creat(final Class<?> interfaceClass){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class<?>[]{interfaceClass},new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ZookeeperRegistryService registryservice = new ZookeeperRegistryService();
                registryservice.connentToRegistryService();
                ServerInfo serverinfo = registryservice.discover(interfaceClass.getName());
                NettyClient nettyClient = new NettyClient(serverinfo.getHost(), serverinfo.getPort());
                RpcRequest rpcrequest = new RpcRequest();
                rpcrequest.setInterfaceName(interfaceClass.getName());
                rpcrequest.setMethodName(method.getName());
                rpcrequest.setParameterTypes(method.getParameterTypes());
                rpcrequest.setParameters(args);
                RpcResponse rpcresponse = nettyClient.getServerMessage(rpcrequest);
                return rpcresponse.getResult();
            }
        });
    }
}
