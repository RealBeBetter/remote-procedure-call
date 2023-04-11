package com.company.rpcspringbootstarter.client.network;

import com.company.rpcspringbootstarter.common.ServiceInterfaceInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 使用Netty网络客户端实现消息发送
 *
 * @author wei.song
 * @since 2023/1/23 11:55
 */
public class NettyRpcClient implements RpcClient {

    @Override
    public byte[] sendMessage(byte[] data, ServiceInterfaceInfo serviceInterfaceInfo) throws InterruptedException {
        final String ip = serviceInterfaceInfo.getIp();
        final Integer port = serviceInterfaceInfo.getPort();

        ClientChannelHandler clientChannelHandler = new ClientChannelHandler(data);
        // 初始化 netty 客户端
        final Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    // 初始化通道，并在通道流水线中注册通道处理器
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        final ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(clientChannelHandler);
                    }
                });
        // 连接服务器端并开始发送消息
        bootstrap.connect(ip, port).sync();
        // 返回服务端响应的消息
        return clientChannelHandler.response();
    }
}
