package com.company.rpcspringbootstarter.config;

/**
 * @author wei.song
 * @since 2023-01-23 12:57
 */
public enum RegisterCenterConstant {

    /**
     * zookeeper注册中心
     */
    ZOOKEEPER("zookeeper"),

    /**
     * nacos注册中心
     */
    NACOS("nacos"),

    ;

    private final String value;

    RegisterCenterConstant(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
