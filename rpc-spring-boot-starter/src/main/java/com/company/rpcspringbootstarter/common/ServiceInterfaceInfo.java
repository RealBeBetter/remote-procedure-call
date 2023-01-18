package com.company.rpcspringbootstarter.common;

import lombok.Builder;
import lombok.Data;

/**
 * 服务提供者对外暴露的信息
 * 服务中每个对外暴露的接口都会当成一种服务能力暴露出去，这个服务能力仅仅是接口能力的简单抽象
 *
 * @author wei.song
 * @since 2023/1/18 14:39
 */
@Data
@Builder
public class ServiceInterfaceInfo {

    /**
     * 服务名（接口全限定名）
     */
    private String serviceName;

    /**
     * 实例 id，每个服务实例不一样
     */
    private String instanceId;

    /**
     * 服务实例 ip 地址，每个实例不一样
     */
    private String ip;

    /**
     * 服务端口号，每个实例一样
     */
    private Integer port;

    /**
     * 实现该接口 bean 对象对应的class 对象，用于后续反射调用
     */
    private Class<?> clazz;

    /**
     * 实现该接口的 bean 对象，用于后续反射调用
     */
    private Object obj;

}
