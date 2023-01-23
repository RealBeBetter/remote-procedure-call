package com.company.rpcspringbootstarter.client.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * @author wei.song
 * @since 2023/1/23 11:56
 */
@Slf4j
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private byte[] response;

    private byte[] data;
    private CountDownLatch countDownLatch;

    public ClientChannelHandler(byte[] data) {
        this.data = data;
        countDownLatch = new CountDownLatch(1);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 通道激活后客户端开始发送数据
        final ByteBuf byteBuf = Unpooled.buffer(data.length);
        byteBuf.writeBytes(data);
        log.info("Rpc client start to send message: {}", byteBuf);
        ctx.writeAndFlush(byteBuf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 客户端接收到信息并开始读取
        log.info("Rpc client received message: {}", msg);
        // 将 ByteBuf 转换为 byte[]
        ByteBuf byteBuf = (ByteBuf) msg;
        response = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(response);
        countDownLatch.countDown();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Rpc client catch exception: {}", cause.getMessage());
        ctx.close();
    }

    public byte[] response() {
        try {
            countDownLatch.await();
        } catch (InterruptedException ignored) {
        }
        return response;
    }
}
