package com.company.rpcspringbootstarter.serialization;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * FastJson serialization protocol: based on Alibaba FastJson
 *
 * @author wei.song
 * @since 2023/1/24 12:17
 */
public class FastJsonMessageProtocol implements MessageProtocol {
    @Override
    public RpcRequest decodeRequestMessage(byte[] data) throws Exception {
        JSONObject jsonObject = JSON.parseObject(data);
        return jsonObject.to(RpcRequest.class);
    }

    @Override
    public byte[] encodeRequestMessage(RpcRequest rpcRequest) throws Exception {
        return JSON.toJSONBytes(rpcRequest);
    }

    @Override
    public RpcResponse decodeResponseMessage(byte[] data) throws Exception {
        JSONObject jsonObject = JSON.parseObject(data);
        return jsonObject.to(RpcResponse.class);
    }

    @Override
    public byte[] encodeResponseMessage(RpcResponse rpcResponse) throws Exception {
        return JSON.toJSONBytes(rpcResponse);
    }
}
