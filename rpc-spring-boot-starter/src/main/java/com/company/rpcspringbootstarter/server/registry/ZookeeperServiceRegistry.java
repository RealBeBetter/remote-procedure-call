package com.company.rpcspringbootstarter.server.registry;

import com.alibaba.fastjson.JSON;
import com.company.rpcspringbootstarter.common.ServiceInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 通过 zookeeper 提供服务注册能力
 *
 * @author wei.song
 * @since 2023/1/20 14:42
 */
@Slf4j
public class ZookeeperServiceRegistry extends DefaultServiceRegistry {

    private ZkClient zkClient;

    public ZookeeperServiceRegistry(String zkAddress) {
        init(zkAddress);
    }

    private void init(String zkAddress) {
        // 初始化，与 Zookeeper 服务器建立连接
        zkClient = new ZkClient(zkAddress);
        // 设置序列化反序列化器
        zkClient.setZkSerializer(new ZkSerializer() {
            @Override
            public byte[] serialize(Object o) throws ZkMarshallingError {
                return String.valueOf(o).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                return new String(bytes, StandardCharsets.UTF_8);
            }
        });
    }

    @Override
    public void register(ServiceInterfaceInfo serviceInterfaceInfo) throws Exception {
        log.info("Registering service: {}", serviceInterfaceInfo);

        super.register(serviceInterfaceInfo);

        // 创建 ZK 永久节点（服务节点）
        String serviceName = serviceInterfaceInfo.getServiceName();
        String servicePath = "/rpc/service/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath, true);
            log.info("Created node: {}", servicePath);
        }

        // 创建 ZK 临时节点（实例节点）
        String uri = JSON.toJSONString(serviceInterfaceInfo);
        uri = URLEncoder.encode(uri, "UTF-8");
        String uriPath = servicePath + "/" + uri;
        if (zkClient.exists(uriPath)) {
            zkClient.delete(uriPath);
        }
        zkClient.createEphemeral(uriPath);
        log.info("Rpc created ephemeral node: {}", uriPath);
    }

}
