package com.company.rpcspringbootstarter.config;

import com.company.rpcspringbootstarter.property.RpcProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wei.song
 * @since 2023/1/19 09:55
 */
@Slf4j
@Configuration
public class AutoConfiguration {

    /**
     * 配置Rpc属性值
     *
     * @return {@link RpcProperties}
     */
    @Bean
    public RpcProperties rpcProperties() {
        return new RpcProperties();
    }

}
