package com.company.rpcspringbootstarter.server;

/**
 * Rpc Service interface
 *
 * @author wei.song
 * @since 2023-01-19 12:46
 */
public interface RpcServer {

    /**
     * 启动服务
     */
    void start();

    /**
     * 停止服务
     */
    void stop();

}
