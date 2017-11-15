package com.gdprpc.rpc.client;

import com.gdprpc.common.bean.RpcRequest;
import com.gdprpc.registry.RegistryService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

/**
 * @author 我是金角大王 on 2017-11-15.
 */
public class RPCInvocationHandler implements InvocationHandler,AnysClientProxy{

    private RegistryService registryService;

    private Class<?> interfaceClass;

    public RPCInvocationHandler(RegistryService registryService, Class<?> interfaceClass) {
        this.registryService = registryService;
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Map<String, Object> map = registryService.discover(interfaceClass.getName());
        ClientChannelInboundHandler clientHandler = NettyClient.getInstance().getHandler(map.get("host").toString(), Integer.parseInt(map.get("prot").toString()));
        RpcRequest rpcrequest = new RpcRequest();
        rpcrequest.setId(UUID.randomUUID().toString());
        rpcrequest.setInterfaceName(interfaceClass.getName());
        rpcrequest.setMethodName(method.getName());
        rpcrequest.setParameterTypes(method.getParameterTypes());
        rpcrequest.setParameters(args);
        RPCFuture future = clientHandler.getServerMessage(rpcrequest);
        return future.get();
    }

    @Override
    public RPCFuture call(String method,Object... args) {
        try{
            Map<String, Object> map = registryService.discover(interfaceClass.getName());
            ClientChannelInboundHandler clientHandler = NettyClient.getInstance().getHandler(map.get("host").toString(), Integer.parseInt(map.get("prot").toString()));
            RpcRequest rpcrequest = new RpcRequest();
            rpcrequest.setId(UUID.randomUUID().toString());
            rpcrequest.setInterfaceName(interfaceClass.getName());
            rpcrequest.setMethodName(method);
            Class[] parameterTypes = new Class[args.length];
            // Get the right class type
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = getClassType(args[i]);
            }
            rpcrequest.setParameterTypes(parameterTypes);
            rpcrequest.setParameters(args);
            RPCFuture future = clientHandler.getServerMessage(rpcrequest);
            return future;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private Class<?> getClassType(Object obj){
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName){
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }

        return classType;
    }

}
