package com.company.rpcspringbootstarter.serialization;

import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wei.song
 * @since 2023/1/18 15:02
 */
@Data
@Builder
public class RpcResponse implements Serializable {
    /**
     * 状态，调用成功或失败
     */
    private String status;
    /**
     * 返回值对象
     */
    private Object retValue;
    /**
     * 返回头
     */
    private Map<String, String> headers = Maps.newHashMap();
    /**
     * 如果失败，返回异常对象
     */
    private Exception exception;
}
