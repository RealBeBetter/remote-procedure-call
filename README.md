# 【项目】RPC 框架

## Remote-Procedure-Call

远程过程调用，RPC 框架，造轮子项目。

项目总体使用 Registry + Netty + JSONObject 的结构开发。

### 流程架构图

![img](https://cdn.staticaly.com/gh/RealBeBetter/image@master/img/202304111958146.png)

## 关键节点

在调用远程方法时，等待方法返回结果时，使用 `CountdownLatch ` 来完成。具体代码：

```java
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
```

之后在 `response ` 方法中，调用的原型为：

```java
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
```

通过初始化流程，设置 `countDownLatch` 的倒计时数。直到在 `channel` 中接收到调用的返回结果，`await` 方法会被调用，最后返回接收结果。

将状态阻塞，实现监听。这里使用了 `CountdownLatch`，一次调用阻塞一次，直到接收到 `Channel` 中的数据。

## 调用流程

1. 服务者启动的时候，注册到注册中心，提供暴露的服务接口、IP、端口号等；
2. 通过服务发现机制选择一个服务提供者暴露的服务；
3. 构造 rpc 请求对象，包括服务名称、方法名称、请求头、参数类型、参数；
4. 编码请求消息，可以配置多种编码方式；
5. 调用 `rpcClient` 开始发送消息；
6. 收到响应消息，解码响应消息；
7. 判断结果是否有误，解析返回结果进行处理。

```java
@SuppressWarnings("unchecked")
public <T> T getProxyInstance(Class<T> clazz) {
    return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 第一步：通过服务发现机制选择一个服务提供者暴露的服务
            String serviceName = clazz.getName();
            ServiceInterfaceInfo serviceInterfaceInfo = serviceDiscovery.selectInstance(serviceName);
            log.info("Rpc server instance list: {}", serviceInterfaceInfo);
            if (serviceInterfaceInfo == null) {
                throw new RpcException("No rpc server found.");
            }

            // 第二步：构造 rpc 请求对象
            final RpcRequest rpcRequest = RpcRequest.builder().serviceName(serviceName).methodName(method.getName())
                    .parameterTypes(method.getParameterTypes()).parameters(args).build();

            // 第三步：编码请求消息，这里可以配置多种编码方式
            byte[] data = messageProtocol.encodeRequestMessage(rpcRequest);

            // 第四步：调用 rpc client 开始发送消息
            byte[] byteResponse = rpcClient.sendMessage(data, serviceInterfaceInfo);

            // 第五步：解码响应消息
            final RpcResponse rpcResponse = messageProtocol.decodeResponseMessage(byteResponse);

            // 第六步：解析返回结果进行处理
            if (rpcResponse.getException() != null) {
                throw rpcResponse.getException();
            }
            return rpcResponse.getRetValue();
        }
    });
}
```
