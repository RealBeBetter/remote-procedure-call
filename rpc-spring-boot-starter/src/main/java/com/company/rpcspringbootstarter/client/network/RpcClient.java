package com.company.rpcspringbootstarter.client.network;

import com.company.rpcspringbootstarter.common.ServiceInterfaceInfo;

/**
 * 网络客户端Client
 *
 * @author wei.song
 * @since 2023-01-23 11:54
 */
public interface RpcClient {

    /**
     * 发送消息，发起调用
     *
     * @param data                 数据
     * @param serviceInterfaceInfo 服务接口信息
     * @return {@link byte[]}
     * @throws InterruptedException 中断异常
     */
    byte[] sendMessage(byte[] data, ServiceInterfaceInfo serviceInterfaceInfo) throws InterruptedException;

}
