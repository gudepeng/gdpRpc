package com.gdprpc.rpc.server;

import com.gdprpc.common.bean.RpcRequest;
import com.gdprpc.common.bean.RpcResponse;
import com.gdprpc.common.bean.ServerInfo;
import com.gdprpc.common.codec.RpcDecoder;
import com.gdprpc.common.codec.RpcEncoder;
import com.gdprpc.registry.RegistryService;
import com.gdprpc.registry.zookeeper.balanceupdateprovider.DefaultBalanceUpdateProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 我是金角大王 on 2017-10-22.
 */
public class DefaultServer implements GServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServer.class);

    /**
     * 注册中心对象
     */
    private RegistryService registryService;
    /**
     * 注册的服务
     */
    private ConcurrentHashMap<String,Object> serviceProviderMap= new ConcurrentHashMap<>();
    /**
     * 注册的服务接口类路径
     */
    private ServerInfo serverInfo = new ServerInfo();

    public DefaultServer(int prot, RegistryService registryService) {
        this.serverInfo.setPort(prot);
        this.registryService = registryService;
    }

    @Override
    public DefaultServer provider(Object serviceProvider) {
        this.serviceProviderMap.put(serviceProvider.getClass().getInterfaces()[0].getName(),serviceProvider);
        return this;
    }

    @Override
    public DefaultServer register() {
        List<String> list = new ArrayList<>();
        serviceProviderMap.forEach((k,v) ->{
            list.add(v.getClass().getInterfaces()[0].getName());
        });
        this.serverInfo.setServicePath(list);
        this.serverInfo.setBalance(1);
        return this;
    }

    @Override
    public DefaultServer start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            // 保持连接数
            b.option(ChannelOption.SO_BACKLOG, 128);
            // 保持连接
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline()
                            .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                            .addLast(new RpcDecoder(RpcRequest.class))
                            .addLast(new RpcEncoder(RpcResponse.class))
                            .addLast(new ServerChannelInboundHandler(serviceProviderMap,new DefaultBalanceUpdateProvider(registryService, serverInfo.getZKPath())));
                }
            });
            // 服务器绑定端口监听
            ChannelFuture f = b.bind(serverInfo.getPort()).sync();
            LOGGER.debug("Server started on port {}", serverInfo.getPort());
            registryService.register(serverInfo);
            // 监听服务器关闭监听
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        return this;
    }
}
