package com.company.rpcspringbootstarter.config;

/**
 * @author wei.song
 * @since 2023-01-23 12:51
 */
public enum SerializationConstant {

    /**
     * 默认序列化方式，Java原生序列化方式
     */
    DEFAULT("java"),

    /**
     * fastjson，使用 fastjson 库完成序列化
     */
    FAST_JSON("fast-json"),

    ;

    private final String value;


    SerializationConstant(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }


}
