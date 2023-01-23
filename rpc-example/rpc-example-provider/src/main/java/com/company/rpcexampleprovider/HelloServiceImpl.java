package com.company.rpcexampleprovider;

import com.company.rpcexampleprovider.service.HelloService;
import com.company.rpcspringbootstarter.annotation.ServiceExpose;

/**
 * Hello World
 * 服务提供者，使用@ServiceExpose注解对外暴露服务
 *
 * @author wei.song
 * @since 2023/1/23 13:21
 */
@ServiceExpose
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "Hello, " + name + "! Your Rpc is running...";
    }
}
