package com.company.rpcspringbootstarter.serialization;

import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wei.song
 * @since 2023/1/18 14:59
 */
@Data
@Builder
public class RpcRequest implements Serializable {
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 请求头
     */
    private Map<String, String> headers = Maps.newHashMap();
    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;
    /**
     * 参数
     */
    private Object[] parameters;
}
