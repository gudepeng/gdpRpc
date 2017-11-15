package com.gdprpc.rpc.client;

import com.gdprpc.common.bean.RpcRequest;
import com.gdprpc.common.bean.RpcResponse;
import com.gdprpc.common.bean.ServerInfo;
import com.gdprpc.registry.RegistryService;
import io.netty.util.concurrent.Future;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.UUID;

/**
 * @author 我是金角大王 on 2017-10-25.
 */
public class GProxy {

    private RegistryService registryService;

    public GProxy(RegistryService registryService) {
        this.registryService = registryService;
    }

    public <T> T creat(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new RPCInvocationHandler(registryService,interfaceClass));
    }

    public AnysClientProxy creatAnys(final Class<?> interfaceClass) {
        return  new RPCInvocationHandler(registryService,interfaceClass);
    }
}
