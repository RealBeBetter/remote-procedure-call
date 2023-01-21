package com.company.rpcspringbootstarter.server.network;

import com.company.rpcspringbootstarter.common.ServiceInterfaceInfo;
import com.company.rpcspringbootstarter.serialization.MessageProtocol;
import com.company.rpcspringbootstarter.serialization.RpcRequest;
import com.company.rpcspringbootstarter.serialization.RpcResponse;
import com.company.rpcspringbootstarter.server.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wei.song
 * @since 2023/1/21 20:05
 */
@Slf4j
public class RequestHandler {

    private final MessageProtocol protocol;
    private final ServiceRegistry serviceRegistry;

    public RequestHandler(MessageProtocol protocol, ServiceRegistry serviceRegistry) {
        this.protocol = protocol;
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * 请求处理程序
     *
     * @param data 数据
     * @return {@link byte[]}
     */
    public byte[] handlerRequest(byte[] data) throws Exception {
        // 请求消息解码
        RpcRequest rpcRequest = protocol.decodeRequestMessage(data);
        String serviceName = rpcRequest.getServiceName();
        ServiceInterfaceInfo serviceInterfaceInfo = serviceRegistry.getRegisteredObject(serviceName);

        RpcResponse rpcResponse = RpcResponse.builder().build();
        if (serviceInterfaceInfo == null) {
            rpcResponse.setStatus(RpcResponse.Status.NOT_FOUND.value());
            return protocol.encodeResponseMessage(rpcResponse);
        }

        try {
            // 通过反射技术调用目标方法
            final Method method = serviceInterfaceInfo.getClazz().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            final Object retValue = method.invoke(serviceInterfaceInfo.getObj(), rpcRequest.getParameters());

            rpcResponse.setRetValue(retValue);
            rpcResponse.setStatus(RpcResponse.Status.SUCCESS.value());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            rpcResponse.setStatus(RpcResponse.Status.FAILED.value());
            rpcResponse.setException(e);
        }
        return protocol.encodeResponseMessage(rpcResponse);
    }

}
