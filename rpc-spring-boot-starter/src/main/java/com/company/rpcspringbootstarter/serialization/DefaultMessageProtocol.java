package com.company.rpcspringbootstarter.serialization;

/**
 * Default serialization protocol: java default serialization
 *
 * @author wei.song
 * @since 2023/1/18 15:09
 */
public class DefaultMessageProtocol implements MessageProtocol {
    @Override
    public RpcRequest decodeRequestMessage(byte[] data) throws Exception {
        return null;
    }

    @Override
    public byte[] encodeRequestMessage(RpcRequest rpcRequest) throws Exception {
        return new byte[0];
    }

    @Override
    public RpcResponse decodeResponseMessage(byte[] data) throws Exception {
        return null;
    }

    @Override
    public byte[] encodeResponseMessage(RpcResponse rpcResponse) throws Exception {
        return new byte[0];
    }
}
