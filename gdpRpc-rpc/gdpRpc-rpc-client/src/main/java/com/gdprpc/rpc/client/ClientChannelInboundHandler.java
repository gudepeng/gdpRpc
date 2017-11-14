package com.gdprpc.rpc.client;

import com.gdprpc.common.bean.RpcRequest;
import com.gdprpc.common.bean.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;

/**
 * @author 我是金角大王 on 2017-10-24.
 */
public class ClientChannelInboundHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private RpcResponse response;
    private volatile Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public RpcResponse getResponse() {
        return this.response;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        this.response = rpcResponse;
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

    public Future<RpcResponse> getServerMessage(RpcRequest rpcRequest){
        try {
            channel.writeAndFlush(rpcRequest).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
