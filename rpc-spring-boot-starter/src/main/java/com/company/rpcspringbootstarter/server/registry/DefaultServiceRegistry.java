package com.company.rpcspringbootstarter.server.registry;

import com.company.rpcspringbootstarter.common.ServiceInterfaceInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地服务注册器，用于本地缓存注册的对象，方便后续获取
 *
 * @author wei.song
 * @since 2023/1/19 21:21
 */
public class DefaultServiceRegistry implements ServiceRegistry {

    private final Map<String, ServiceInterfaceInfo> LOCAL_MAP = new HashMap<>();
    protected String protocol;
    protected Integer port;

    @Override
    public void register(ServiceInterfaceInfo serviceInterfaceInfo) throws Exception {
        if (serviceInterfaceInfo == null) {
            throw new IllegalArgumentException("Register param can't be null.");
        }

        String serviceName = serviceInterfaceInfo.getServiceName();
        LOCAL_MAP.put(serviceName, serviceInterfaceInfo);
    }

    @Override
    public ServiceInterfaceInfo getRegisteredObject(String serviceName) throws Exception {
        return LOCAL_MAP.get(serviceName);
    }
}
