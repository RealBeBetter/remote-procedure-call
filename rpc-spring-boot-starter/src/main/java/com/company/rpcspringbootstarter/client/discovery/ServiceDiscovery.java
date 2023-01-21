package com.company.rpcspringbootstarter.client.discovery;

import com.company.rpcspringbootstarter.common.ServiceInterfaceInfo;

/**
 * 客户端服务发现
 *
 * @author wei.song
 * @since 2023/1/21 20:54
 */
public interface ServiceDiscovery {

    /**
     * 通过服务名称随机选择一个健康的实例
     *
     * @param serviceName 服务名称
     * @return {@link ServiceInterfaceInfo}
     */
    ServiceInterfaceInfo selectInstance(String serviceName);

}
