package com.company.rpcspringbootstarter.serialization;

import java.io.*;

/**
 * Default serialization protocol: java default serialization
 *
 * @author wei.song
 * @since 2023/1/18 15:09
 */
public class DefaultMessageProtocol implements MessageProtocol {
    @Override
    public RpcRequest decodeRequestMessage(byte[] data) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
        inputStream.close();
        objectInputStream.close();
        return rpcRequest;
    }

    @Override
    public byte[] encodeRequestMessage(RpcRequest rpcRequest) throws Exception {
        return serialize(rpcRequest);
    }

    @Override
    public RpcResponse decodeResponseMessage(byte[] data) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
        inputStream.close();
        objectInputStream.close();
        return rpcResponse;
    }

    @Override
    public byte[] encodeResponseMessage(RpcResponse rpcResponse) throws Exception {
        return serialize(rpcResponse);
    }

    /**
     * 序列化
     *
     * @param object 对象
     * @return {@link byte[]}
     * @throws IOException IO异常
     */
    private byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
        out.writeObject(object);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        out.close();
        byteArrayOutputStream.close();
        return bytes;
    }
}
