package com.gdprpc.rpc.client;

import com.gdprpc.common.bean.RpcRequest;
import com.gdprpc.common.bean.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 我是金角大王 on 2017-10-24.
 */
public class ClientChannelInboundHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private volatile Channel channel;
    private Map<String,RPCFuture> futureMap = new ConcurrentHashMap<>();

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        RPCFuture future = futureMap.get(rpcResponse.getId());
        if (future != null) {
            futureMap.remove(rpcResponse.getId());
            future.done(rpcResponse);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    public RPCFuture getServerMessage(RpcRequest rpcRequest){
        RPCFuture future = new RPCFuture(rpcRequest);
        futureMap.put(rpcRequest.getId(),future);
        channel.writeAndFlush(rpcRequest);
        return future;
    }

}
