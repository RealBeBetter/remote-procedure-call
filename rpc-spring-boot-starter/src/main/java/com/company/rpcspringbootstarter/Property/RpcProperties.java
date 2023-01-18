package com.company.rpcspringbootstarter.Property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wei.song
 * @since 2023/1/18 14:51
 */
@Data
@Component
@ConfigurationProperties(prefix = "rpc")
public class RpcProperties {
    /**
     * 暴露端口，默认 6666
     */
    private Integer exposePort = 6666;

    /**
     * 注册
     */
    private String register;

    /**
     * 注册地址
     */
    private String registerAddress;

    /**
     * 序列化协议
     */
    private String protocol = "java";
}
