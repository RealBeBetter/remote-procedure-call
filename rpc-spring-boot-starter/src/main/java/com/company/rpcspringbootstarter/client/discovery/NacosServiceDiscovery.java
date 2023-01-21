package com.company.rpcspringbootstarter.client.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.company.rpcspringbootstarter.common.ServiceInterfaceInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wei.song
 * @since 2023/1/21 20:56
 */
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery {

    private NamingService namingService;

    public NacosServiceDiscovery(String serverList) {
        // 使用工厂类创建注册中心对象，构造参数为Nacos Server的ip地址，连接 Nacos 服务器
        try {
            namingService = NamingFactory.createNamingService(serverList);
        } catch (NacosException e) {
            log.error("Rpc nacos client init error", e);
        }
        log.info("Rpc nacos server status: {}", namingService.getServerStatus());
    }

    @Override
    public ServiceInterfaceInfo selectInstance(String serviceName) {
        Instance instance;
        try {
            // 调用 nacos 提供的接口，随机挑选一个服务实例，负载均衡的算法依赖 nacos 的实现
            instance = namingService.selectOneHealthyInstance(serviceName);
        } catch (NacosException e) {
            log.error("Nacos exception", e);
            return null;
        }

        // 封装实例对象返回
        return ServiceInterfaceInfo.builder()
                .serviceName(instance.getServiceName())
                .ip(instance.getIp())
                .port(instance.getPort())
                .build();
    }
}
