package com.gdprpc.rpc.client;

/**
 * @author 我是金角大王 on 2017-11-15.
 */
public interface AnysClientProxy {

    RPCFuture call(String funcName, Object... args);
}
