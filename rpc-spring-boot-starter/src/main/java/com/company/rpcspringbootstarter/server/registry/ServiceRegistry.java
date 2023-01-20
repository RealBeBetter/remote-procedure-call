package com.company.rpcspringbootstarter.server.registry;

import com.company.rpcspringbootstarter.common.ServiceInterfaceInfo;

/**
 * @author wei.song
 * @since 2023-01-19 21:19
 */
public interface ServiceRegistry {

    /**
     * 注册接口信息
     *
     * @param serviceInterfaceInfo 待注册的服务接口信息
     * @throws Exception 异常
     */
    void register(ServiceInterfaceInfo serviceInterfaceInfo) throws Exception;

    /**
     * 根据服务名称和接口名称获取已注册的对象
     *
     * @param serviceName 服务名称
     * @return {@link ServiceInterfaceInfo}
     * @throws Exception 异常
     */
    ServiceInterfaceInfo getRegisteredObject(String serviceName) throws Exception;

}
