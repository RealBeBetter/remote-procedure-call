package com.company.rpcspringbootstarter.listener;

import com.company.rpcspringbootstarter.annotation.ServiceExpose;
import com.company.rpcspringbootstarter.annotation.ServiceReference;
import com.company.rpcspringbootstarter.client.ClientProxyFactory;
import com.company.rpcspringbootstarter.common.ServiceInterfaceInfo;
import com.company.rpcspringbootstarter.property.RpcProperties;
import com.company.rpcspringbootstarter.server.network.RpcServer;
import com.company.rpcspringbootstarter.server.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author wei.song
 * @since 2023/1/19 12:44
 */
@Slf4j
public class DefaultRpcListener implements ApplicationListener<ContextRefreshedEvent> {

    private final ServiceRegistry serviceRegistry;
    private final RpcServer rpcServer;
    private final ClientProxyFactory clientProxyFactory;
    private final RpcProperties rpcProperties;

    public DefaultRpcListener(ServiceRegistry serviceRegistry, RpcServer rpcServer,
                              ClientProxyFactory clientProxyFactory, RpcProperties rpcProperties) {
        this.serviceRegistry = serviceRegistry;
        this.rpcServer = rpcServer;
        this.clientProxyFactory = clientProxyFactory;
        this.rpcProperties = rpcProperties;
    }

    private String getLocalAddress() {
        String ip = "127.0.0.1";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ignored) {
        }
        return ip;
    }

    /**
     * 注册界面实例信息
     *
     * @param beanName bean名字
     * @param bean     Bean对象
     */
    private void registerInstanceInterfaceInfo(String beanName, Object bean) {
        final Class<?>[] interfaces = bean.getClass().getInterfaces();
        if (interfaces.length <= 0) {
            // 注解类未实现接口，直接返回
            return;
        }

        // 暂时只考虑实现了一个接口的场景
        Class<?> interfaceClazz = InterfaceRoutePolicy.selectInterfaceClass(interfaces);
        String serviceName = interfaceClazz.getName();
        String ip = getLocalAddress();
        Integer port = rpcProperties.getExposePort();

        ServiceInterfaceInfo serviceInterfaceInfo = ServiceInterfaceInfo.builder().serviceName(serviceName)
                .ip(ip).port(port).clazz(interfaceClazz).obj(bean).build();

        try {
            // 注册服务
            serviceRegistry.register(serviceInterfaceInfo);
        } catch (Exception e) {
            log.error("Rpc fail to register service, beanName: {}, error: {}", beanName, e.getMessage());
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        final ApplicationContext applicationContext = event.getApplicationContext();
        // 如果是 root application context就开始执行
        if (applicationContext.getParent() == null) {
            // 初始化 rpc 服务端
            initRpcServer(applicationContext);
            // 初始化 rpc 客户端
            initRpcClient(applicationContext);
        }
    }


    private void initRpcServer(ApplicationContext applicationContext) {
        // 扫描服务端 @ServiceExpose 注解，并将服务接口信息注册到注册中心
        final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ServiceExpose.class);
        if (beans.size() == 0) {
            // 未发现注解，直接返回
            return;
        }

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            // 注册服务实例接口信息
            registerInstanceInterfaceInfo(entry.getKey(), entry.getValue());
        }

        // 启动网络通信服务器，开始监听指定端口
        rpcServer.start();
    }


    private void initRpcClient(ApplicationContext applicationContext) {
        // 遍历容器中所有的 Bean
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Class<?> clazz = applicationContext.getType(beanName);
            if (clazz == null) {
                continue;
            }

            // 遍历每个 bean 的成员属性，如果成员属性被 @ServiceReference 注解标记，说明依赖rpc远端接口
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                final ServiceReference annotation = field.getAnnotation(ServiceReference.class);
                if (annotation == null) {
                    // 如果该成员属性没有标记该注解，继续找一下
                    continue;
                }

                // 找到被注解标记的成员属性
                Object beanObject = applicationContext.getBean(beanName);
                Class<?> fieldClass = field.getType();
                try {
                    // 注入代理对象值
                    field.setAccessible(true);
                    field.set(beanObject, clientProxyFactory.getProxyInstance(fieldClass));
                } catch (IllegalAccessException e) {
                    log.error("Rpc fail to inject service, beanName: {}, error: {}", beanName, e.getMessage());
                }
            }
        }
    }
}
