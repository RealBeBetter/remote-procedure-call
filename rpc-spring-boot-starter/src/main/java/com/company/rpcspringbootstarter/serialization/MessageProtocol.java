package com.company.rpcspringbootstarter.serialization;

/**
 * The interface Message protocol
 *
 * @author wei.song
 * @since 2023-01-18 14:58
 */
public interface MessageProtocol {

    /**
     * 解码请求信息
     *
     * @param data 客户端请求数据，字节数组格式
     * @return {@link RpcRequest}
     * @throws Exception 异常
     */
    RpcRequest decodeRequestMessage(byte[] data) throws Exception;

    /**
     * 对请求消息进行编码
     *
     * @param rpcRequest rpc请求
     * @return {@link byte[]}
     * @throws Exception 异常
     */
    byte[] encodeRequestMessage(RpcRequest rpcRequest) throws Exception;


    /**
     * 解码响应消息
     *
     * @param data 服务端返回数据，字节数组格式
     * @return {@link RpcResponse}
     * @throws Exception 异常
     */
    RpcResponse decodeResponseMessage(byte[] data) throws Exception;

    /**
     * 编码响应消息
     *
     * @param rpcResponse rpc响应
     * @return {@link byte[]}
     * @throws Exception 异常
     */
    byte[] encodeResponseMessage(RpcResponse rpcResponse) throws Exception;

}
