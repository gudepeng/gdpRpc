package com.gdprpc.rpc.client;

import com.gdprpc.common.bean.RpcRequest;
import com.gdprpc.common.bean.RpcResponse;
import com.gdprpc.common.codec.RpcDecoder;
import com.gdprpc.common.codec.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author 我是金角大王 on 2017-10-25.
 */
public class NettyClient {

    private static NettyClient nettyClient;

    private EventLoopGroup group = new NioEventLoopGroup();

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

    private Map<String, ClientChannelInboundHandler> connectedServerNodes = new ConcurrentHashMap<>();

    final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static NettyClient getInstance() {
        if (nettyClient == null) {
            synchronized (NettyClient.class) {
                if (nettyClient == null) {
                    nettyClient = new NettyClient();
                }
            }
        }
        return nettyClient;
    }

    public ClientChannelInboundHandler getHandler(String host, int port) throws InterruptedException {
        String key = "/" + host + ":" + port;
        synchronized (NettyClient.class) {
            if (connectedServerNodes.containsKey(key)) {
                return connectedServerNodes.get(key);
            } else {
                connectServer(host, port);
                countDownLatch.await();
                return connectedServerNodes.get(key);
            }
        }
    }

    public void connectServer(final String host, final int port) {
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap b = new Bootstrap();
                b.group(group);
                b.channel(NioSocketChannel.class);
                // 有数据立即发送
                b.option(ChannelOption.TCP_NODELAY, true);
                // 保持连接
                b.option(ChannelOption.SO_KEEPALIVE, true);
                b.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcEncoder(RpcRequest.class))
                                .addLast(new RpcDecoder(RpcResponse.class))
                                .addLast(new ClientChannelInboundHandler());
                    }
                });
                // 连接服务端
                ChannelFuture ch = b.connect(host, port).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        ClientChannelInboundHandler handler = channelFuture.channel().pipeline().get(ClientChannelInboundHandler.class);
                        InetSocketAddress remoteAddress = (InetSocketAddress) handler.getChannel().remoteAddress();
                        connectedServerNodes.put(remoteAddress.getAddress() + ":" + remoteAddress.getPort(), handler);
                        countDownLatch.countDown();
                    }
                });
            }
        });
    }
}
