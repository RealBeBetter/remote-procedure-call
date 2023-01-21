package com.company.rpcspringbootstarter.client.discovery;

import com.alibaba.fastjson.JSON;
import com.company.rpcspringbootstarter.common.ServiceInterfaceInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wei.song
 * @since 2023/1/21 20:57
 */
@Slf4j
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private ZkClient zkClient;

    public ZookeeperServiceDiscovery(String zkAddress) {
        zkClient = new ZkClient(zkAddress);
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
    public ServiceInterfaceInfo selectInstance(String serviceName) {
        final String servicePath = "/rpc/service/" + serviceName;
        final List<String> childrenNodes = zkClient.getChildren(servicePath);

        return Optional.ofNullable(childrenNodes)
                .orElse(Lists.newArrayList())
                .stream().map(node -> {
                    try {
                        // 将服务信息经过URL解码后反序列化为对象
                        String serviceInstanceJson = URLDecoder.decode(node, "UTF-8");
                        return JSON.parseObject(serviceInstanceJson, ServiceInterfaceInfo.class);
                    } catch (UnsupportedEncodingException e) {
                        log.error("Rpc Zookeeper fail to decode", e);
                    }
                    return null;
                }).filter(Objects::nonNull).findAny().orElse(null);
    }
}
