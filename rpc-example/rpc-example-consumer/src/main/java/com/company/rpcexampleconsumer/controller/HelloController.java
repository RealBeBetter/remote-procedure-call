package com.company.rpcexampleconsumer.controller;

import com.company.rpcexampleprovider.service.HelloService;
import com.company.rpcspringbootstarter.annotation.ServiceReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务消费者，用于模拟客户端调用远端服务
 *
 * @author wei.song
 * @since 2023/1/23 13:26
 */
@Slf4j
@RestController
public class HelloController {

    /**
     * 通过@ServiceReference注解将远程服务注入到本地
     */
    @ServiceReference
    private HelloService helloService;

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        // 这里调用的是远端服务的接口，使用上与调本地服务一样
        final String rsp = helloService.hello(name);
        log.info("Receive message from rpc server, msg: {}", rsp);
        return rsp;
    }

}
