package com.company.rpcspringbootstarter.client;

import com.company.rpcspringbootstarter.client.discovery.ServiceDiscovery;
import com.company.rpcspringbootstarter.client.network.RpcClient;
import com.company.rpcspringbootstarter.common.ServiceInterfaceInfo;
import com.company.rpcspringbootstarter.exception.RpcException;
import com.company.rpcspringbootstarter.serialization.MessageProtocol;
import com.company.rpcspringbootstarter.serialization.RpcRequest;
import com.company.rpcspringbootstarter.serialization.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author wei.song
 * @since 2023/1/23 12:09
 */
@Slf4j
public class ClientProxyFactory {

    private final ServiceDiscovery serviceDiscovery;
    private final MessageProtocol messageProtocol;
    private final RpcClient rpcClient;

    public ClientProxyFactory(ServiceDiscovery serviceDiscovery, MessageProtocol messageProtocol, RpcClient rpcClient) {
        this.serviceDiscovery = serviceDiscovery;
        this.messageProtocol = messageProtocol;
        this.rpcClient = rpcClient;
    }

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

}
