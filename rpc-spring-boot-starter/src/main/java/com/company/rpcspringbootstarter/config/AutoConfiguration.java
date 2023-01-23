package com.company.rpcspringbootstarter.config;

import com.company.rpcspringbootstarter.client.ClientProxyFactory;
import com.company.rpcspringbootstarter.client.discovery.NacosServiceDiscovery;
import com.company.rpcspringbootstarter.client.discovery.ServiceDiscovery;
import com.company.rpcspringbootstarter.client.discovery.ZookeeperServiceDiscovery;
import com.company.rpcspringbootstarter.client.network.NettyRpcClient;
import com.company.rpcspringbootstarter.listener.DefaultRpcListener;
import com.company.rpcspringbootstarter.property.RpcProperties;
import com.company.rpcspringbootstarter.serialization.DefaultMessageProtocol;
import com.company.rpcspringbootstarter.serialization.MessageProtocol;
import com.company.rpcspringbootstarter.server.network.NettyRpcServer;
import com.company.rpcspringbootstarter.server.network.RequestHandler;
import com.company.rpcspringbootstarter.server.network.RpcServer;
import com.company.rpcspringbootstarter.server.registry.DefaultServiceRegistry;
import com.company.rpcspringbootstarter.server.registry.NacosServiceRegistry;
import com.company.rpcspringbootstarter.server.registry.ServiceRegistry;
import com.company.rpcspringbootstarter.server.registry.ZookeeperServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 客户端代理工厂
     *
     * @param serviceDiscovery 服务发现
     * @return {@link ClientProxyFactory}
     */
    @Bean
    public ClientProxyFactory clientProxyFactory(@Autowired ServiceDiscovery serviceDiscovery) {
        return new ClientProxyFactory(serviceDiscovery, new DefaultMessageProtocol(), new NettyRpcClient());
    }

    /**
     * 请求处理程序
     *
     * @param rpcProperties   rpc配置属性
     * @param serviceRegistry 服务注册中心
     * @return {@link RequestHandler}
     */
    @Bean
    public RequestHandler requestHandler(@Autowired RpcProperties rpcProperties, @Autowired ServiceRegistry serviceRegistry) {
        final String protocol = rpcProperties.getProtocol();
        MessageProtocol messageProtocol = new DefaultMessageProtocol();
        // 暂时只支持 java 自带的序列化方式，可以自行进行扩展，比如：fastjson 等
        if (SerializationConstant.DEFAULT.value().equalsIgnoreCase(protocol)) {
            messageProtocol = new DefaultMessageProtocol();
        }
        return new RequestHandler(messageProtocol, serviceRegistry);
    }

    /**
     * 服务发现
     *
     * @param rpcProperties rpc配置属性
     * @return {@link ServiceDiscovery}
     */
    @Bean
    public ServiceDiscovery serviceDiscovery(@Autowired RpcProperties rpcProperties) {
        final String register = rpcProperties.getRegister();
        final String registerAddress = rpcProperties.getRegisterAddress();
        if (RegisterCenterConstant.NACOS.value().equalsIgnoreCase(register)) {
            log.info("Rpc Nacos Discovery is active.");
            return new NacosServiceDiscovery(registerAddress);
        } else if (RegisterCenterConstant.ZOOKEEPER.value().equalsIgnoreCase(register)) {
            log.info("Rpc Zookeeper Discovery is active.");
            return new ZookeeperServiceDiscovery(registerAddress);
        }
        return null;
    }

    /**
     * 服务注册
     *
     * @param rpcProperties rpc配置属性
     * @return {@link ServiceRegistry}
     */
    @Bean
    public ServiceRegistry serviceRegister(@Autowired RpcProperties rpcProperties) {
        // 根据参数配置自动选择注册中心
        final String register = rpcProperties.getRegister();
        final String registerAddress = rpcProperties.getRegisterAddress();
        if (RegisterCenterConstant.NACOS.value().equalsIgnoreCase(register)) {
            log.info("Rpc Nacos register is active.");
            return new NacosServiceRegistry(registerAddress);
        } else if (RegisterCenterConstant.ZOOKEEPER.value().equalsIgnoreCase(register)) {
            log.info("Rpc Zookeeper register is active.");
            return new ZookeeperServiceRegistry(registerAddress);
        } else {
            log.info("Rpc Default register is active.");
            return new DefaultServiceRegistry();
        }
    }

    /**
     * rpc服务器
     *
     * @param rpcProperties  rpc属性
     * @param requestHandler 请求处理程序
     * @return {@link RpcServer}
     */
    @Bean
    public RpcServer rpcServer(@Autowired RpcProperties rpcProperties, @Autowired RequestHandler requestHandler) {
        return new NettyRpcServer(rpcProperties.getExposePort(), requestHandler);
    }

    /**
     * 默认rpc监听器
     *
     * @param serviceRegistry    服务注册中心
     * @param rpcServer          rpc服务器
     * @param clientProxyFactory 客户机代理工厂
     * @param rpcProperties      rpc特性
     * @return {@link DefaultRpcListener}
     */
    @Bean
    public DefaultRpcListener defaultRpcListener(@Autowired ServiceRegistry serviceRegistry,
                                                 @Autowired RpcServer rpcServer,
                                                 @Autowired ClientProxyFactory clientProxyFactory,
                                                 @Autowired RpcProperties rpcProperties) {
        return new DefaultRpcListener(serviceRegistry, rpcServer, clientProxyFactory, rpcProperties);
    }

}
