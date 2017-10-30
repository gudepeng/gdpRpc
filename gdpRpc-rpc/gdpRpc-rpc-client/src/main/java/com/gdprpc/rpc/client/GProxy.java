package com.gdprpc.rpc.client;

import com.gdprpc.common.bean.RpcRequest;
import com.gdprpc.common.bean.RpcResponse;
import com.gdprpc.common.bean.ServerInfo;
import com.gdprpc.registry.RegistryService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author 我是金角大王 on 2017-10-25.
 */
public class GProxy {

    private RegistryService registryService;

    public GProxy(RegistryService registryService) {
        this.registryService = registryService;
    }

    public <T> T creat(final Class<?> interfaceClass){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class<?>[]{interfaceClass},new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ServerInfo serverinfo = registryService.discover(interfaceClass.getName());
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
