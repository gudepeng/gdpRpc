package com.gdprpc.rpc.server;

import com.gdprpc.common.bean.RpcRequest;
import com.gdprpc.common.bean.RpcResponse;
import com.gdprpc.registry.zookeeper.balanceupdateprovider.BalanceUpdateProvider;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * @author 我是金角大王 on 2017-10-27.
 */
public class ServerChannelInboundHandler extends SimpleChannelInboundHandler<RpcRequest> {
    /**
     * 注册的服务
     */
    private final ConcurrentHashMap<String,Object> serviceProvider;
    private BalanceUpdateProvider balanceupdateprovider;
    private final  Integer BALANCE_STEP = 1;
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));


    public ServerChannelInboundHandler(ConcurrentHashMap serviceProvider,BalanceUpdateProvider balanceupdateprovider) {
        super();
        this.serviceProvider = serviceProvider;
        this.balanceupdateprovider = balanceupdateprovider;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    // 收到消息直接打印输出
                    System.out.println(ctx.channel().remoteAddress() + "客戶端消息 :" + request.getInterfaceName());
                    Method method = serviceProvider.get(request.getInterfaceName()).getClass().getMethod(request.getMethodName(),request.getParameterTypes() );
                    //执行方法
                    Object returnValue=method.invoke(serviceProvider.get(request.getInterfaceName()) , request.getParameters());
                    RpcResponse response = new RpcResponse();
                    response.setId(request.getId());
                    response.setResult(returnValue);
                    ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) {
                            System.out.println("返回");
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "客户端发来链接");
        //balanceupdateprovider.addBalance(BALANCE_STEP);
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
        //balanceupdateprovider.reduceBalance(BALANCE_STEP);
        super.channelInactive(ctx);
    }
}
