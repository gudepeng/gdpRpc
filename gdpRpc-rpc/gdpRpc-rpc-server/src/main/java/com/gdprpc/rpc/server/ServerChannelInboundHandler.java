package com.gdprpc.rpc.server;

import com.gdprpc.common.bean.RpcRequest;
import com.gdprpc.common.bean.RpcResponse;
import com.gdprpc.registry.zookeeper.BalanceUpdateProvider.BalanceUpdateProvider;
import com.gdprpc.registry.zookeeper.ZookeeperRegistryService;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by 我是金角大王 on 2017-10-27.
 */
public class ServerChannelInboundHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private BalanceUpdateProvider balanceupdateprovider;
    private final  Integer BALANCE_STEP = 1;

    public ServerChannelInboundHandler(BalanceUpdateProvider balanceupdateprovider) {
        super();
        this.balanceupdateprovider = balanceupdateprovider;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        // 收到消息直接打印输出
        System.out.println(ctx.channel().remoteAddress() + "客戶端消息 :" + request.getInterfaceName());
        // 收到消息直接打印输出
        RpcResponse response = new RpcResponse();
        response.setResult("服务端成功执行");
        // 返回客户端消息 - 我已经接收到了你的消息
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        ;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "客户端发来链接");
        balanceupdateprovider.addBalance(BALANCE_STEP);
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("发生错误");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端关闭");
        balanceupdateprovider.reduceBalance(BALANCE_STEP);
        super.channelInactive(ctx);
    }
}
