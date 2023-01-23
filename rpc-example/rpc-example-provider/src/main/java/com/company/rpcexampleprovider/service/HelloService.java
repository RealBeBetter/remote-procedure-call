package com.company.rpcexampleprovider.service;

/**
 * @author wei.song
 * @since 2023-01-23 13:21
 */
public interface HelloService {

    /**
     * Greet
     *
     * @param name 名字
     * @return {@link String}
     */
    public String hello(String name);

}
