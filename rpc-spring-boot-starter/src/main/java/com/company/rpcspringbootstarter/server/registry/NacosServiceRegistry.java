package com.company.rpcspringbootstarter.server.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.company.rpcspringbootstarter.common.ServiceInterfaceInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * 通过 Nacos 提供服务注册能力
 *
 * @author wei.song
 * @since 2023/1/20 14:37
 */
@Slf4j
public class NacosServiceRegistry extends DefaultServiceRegistry {

    private NamingService namingService;

    public NacosServiceRegistry(String serverList) {
        // 使用工厂类创建注册中心对象，构造参数为 NacosServer 的 ip 地址，连接 Nacos 服务器
        try {
            namingService = NamingFactory.createNamingService(serverList);
        } catch (NacosException e) {
            log.error("Nacos init error", e);
        }
        // 打印 Nacos 的运行状态
        log.info("Nacos server status: {}", namingService.getServerStatus());
    }

    @Override
    public void register(ServiceInterfaceInfo serviceInterfaceInfo) throws Exception {
        super.register(serviceInterfaceInfo);
        // 注册当前服务实例
        Instance instance = buildInstance(serviceInterfaceInfo);
        namingService.registerInstance(serviceInterfaceInfo.getServiceName(), instance);
    }

    private Instance buildInstance(ServiceInterfaceInfo serviceInterfaceInfo) {
        // 将实例信息注册到 Nacos 中心
        Instance instance = new Instance();
        instance.setIp(serviceInterfaceInfo.getIp());
        instance.setPort(serviceInterfaceInfo.getPort());
        // add more metadata
        return instance;
    }
}
