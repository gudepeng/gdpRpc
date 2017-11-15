package com.gdprpc.rpc.client;


import com.gdprpc.common.bean.RpcRequest;
import com.gdprpc.common.bean.RpcResponse;

import java.util.concurrent.*;


/**
 * @author 我是金角大王 on 2017-11-15.
 */
public class RPCFuture{

    private RpcRequest rpcRequest;

    private RpcResponse rpcResponse;

    final CountDownLatch countDownLatch = new CountDownLatch(1);

    public RPCFuture(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
    }

    public Object get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        if (this.rpcResponse != null) {
            return this.rpcResponse.getResult();
        } else {
            return null;
        }
    }

    public void done(RpcResponse reponse) {
        this.rpcResponse = reponse;
        countDownLatch.countDown();
    }
}
