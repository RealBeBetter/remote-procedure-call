package com.company.rpcspringbootstarter.server.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;


/**
 * @author wei.song
 * @since 2023/1/19 21:16
 */
@Slf4j
public class NettyRpcServer implements RpcServer {

    private final Integer port;
    private final RequestHandler requestHandler;

    private Channel channel;

    public NettyRpcServer(Integer port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
    }


    @Override
    public void start() {
        EventLoopGroup headGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    // 设置两个线程组
                    .group(headGroup, workerGroup)
                    // 设置服务端通道实现类型
                    .channel(NioServerSocketChannel.class)
                    // 服务端用于接收进来的连接，也就是boosGroup线程, 线程队列大小
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // child 通道，worker 线程处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 给 pipeline 管道设置自定义的处理器
                        @Override
                        public void initChannel(SocketChannel channel) {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new ChannelRequestHandler());
                        }
                    });

            // 绑定端口号，同步启动服务
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            Channel channel = channelFuture.channel();
            log.info("[Rpc] Rpc Server start on port: {}", port);
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("Rpc server error: ", e);
        } finally {
            // 释放线程组资源
            headGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        channel.close();
    }

    public class ChannelRequestHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("channel active: {}", ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("Rpc Server receive message: {}", msg);
            final ByteBuf byteBuf = (ByteBuf) msg;
            // 将channel收到的信息读取到byte数组中
            final byte[] requestBytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(requestBytes);
            // 处理请求消息
            final byte[] responseBytes = requestHandler.handlerRequest(requestBytes);
            log.info("Rpc Server response message: {}", responseBytes);
            // 将返回byte写回到channel的缓存中
            final ByteBuf buffer = Unpooled.buffer(responseBytes.length);
            buffer.writeBytes(responseBytes);
            ctx.writeAndFlush(buffer);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("Rcp caught error: ", cause);
            ctx.close();
        }
    }


}
